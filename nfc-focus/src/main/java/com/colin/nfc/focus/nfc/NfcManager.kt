package com.colin.nfc.focus.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.colin.nfc.focus.api.NfcScanResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.nio.charset.StandardCharsets

/**
 * NFC 标签管理器
 * 
 * 负责 NFC 标签的读取和写入操作。
 * 支持 NDEF 格式的标签读写，以及格式化未格式化的标签。
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class NfcManager(private val context: Context) {
    
    companion object {
        private const val TAG = "NfcManager"
    }
    
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    
    init {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        
        if (nfcAdapter == null) {
            Log.w(TAG, "设备不支持 NFC")
        } else if (!nfcAdapter!!.isEnabled) {
            Log.w(TAG, "NFC 功能未启用")
        }
    }
    
    /**
     * 检查 NFC 是否可用
     */
    fun isNfcAvailable(): Boolean {
        return (nfcAdapter != null) && (nfcAdapter?.isEnabled == true)
    }
    
    /**
     * 扫描 NFC 标签（返回 Flow）
     * 
     * @param activity 当前 Activity（用于前台 NFC 读取）
     * @return Flow<NfcScanResult> 扫描结果流
     */
    fun scanTag(activity: Activity): Flow<NfcScanResult> = callbackFlow {
        if (!isNfcAvailable()) {
            trySend(NfcScanResult.Error("NFC 不可用"))
            close()
            return@callbackFlow
        }
        
        // 创建 PendingIntent
        val intent = Intent(activity, activity.javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(
            activity, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // 设置 NFC 意图过滤器
        val filters = arrayOf(
            IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
            IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        )
        
        // 注册前台 NFC 读取
        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, filters, null)
        
        Log.d(TAG, "NFC 前台调度已启用")
        
        // 处理 NFC 意图
        // activity.window.decorView.post {
        //     // 等待 NFC 标签被扫描
        // }
        
        awaitClose {
            // 禁用前台调度
            nfcAdapter?.disableForegroundDispatch(activity)
            Log.d(TAG, "NFC 前台调度已禁用")
        }
    }
    
    /**
     * 从 Intent 中解析 NFC 标签
     * 
     * @param intent NFC 意图
     * @return 标签 UID 或 null
     */
    fun parseTagFromIntent(intent: Intent?): String? {
        if (intent == null) return null
        
        val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
        return tag?.let { bytesToHexString(it.id) }
    }
    
    /**
     * 读取 NFC 标签内容
     * 
     * @param tag NFC 标签
     * @return 标签内容或 null
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun readTag(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null
        
        return try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            
            if (ndefMessage == null || ndefMessage.records.isEmpty()) {
                Log.d(TAG, "标签为空")
                return null
            }
            
            val record = ndefMessage.records[0]
            val payload = record.payload
            
            // 跳过语言代码前缀（通常是3字节）
            val languageCodeLength = payload[0].toInt() and 0x3F
            val text = String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, StandardCharsets.UTF_8)
            
            Log.d(TAG, "读取到标签内容: $text")
            text
        } catch (e: Exception) {
            Log.e(TAG, "读取标签失败", e)
            null
        } finally {
            try {
                ndef.close()
            } catch (e: Exception) {
                Log.e(TAG, "关闭 NDEF 连接失败", e)
            }
        }
    }
    
    /**
     * 写入数据到 NFC 标签
     * 
     * @param tag NFC 标签
     * @param data 要写入的数据
     * @return 是否写入成功
     */
    fun writeTag(tag: Tag, data: String): Boolean {
        val ndef = Ndef.get(tag)
        
        return if (ndef != null) {
            // 标签已经是 NDEF 格式
            writeNdefTag(ndef, data)
        } else {
            // 尝试格式化标签
            val ndefFormatable = NdefFormatable.get(tag)
            if (ndefFormatable != null) {
                formatAndWriteTag(ndefFormatable, data)
            } else {
                Log.e(TAG, "标签不支持 NDEF 格式")
                false
            }
        }
    }
    
    /**
     * 写入 NDEF 标签
     */
    private fun writeNdefTag(ndef: Ndef, data: String): Boolean {
        return try {
            ndef.connect()
            
            // 检查是否可写
            if (!ndef.isWritable) {
                Log.e(TAG, "标签不可写")
                return false
            }
            
            // 创建 NDEF 消息
            val mimeBytes = "text/plain".toByteArray(StandardCharsets.US_ASCII)
            val payload = data.toByteArray(StandardCharsets.UTF_8)
            val record = android.nfc.NdefRecord(
                android.nfc.NdefRecord.TNF_MIME_MEDIA,
                mimeBytes,
                ByteArray(0),
                payload
            )
            
            val message = android.nfc.NdefMessage(arrayOf(record))
            
            // 检查大小限制
            val size = message.toByteArray().size
            if (size > ndef.maxSize) {
                Log.e(TAG, "数据过大: $size > ${ndef.maxSize}")
                return false
            }
            
            // 写入数据
            ndef.writeNdefMessage(message)
            
            Log.d(TAG, "数据写入成功")
            true
        } catch (e: Exception) {
            Log.e(TAG, "写入标签失败", e)
            false
        } finally {
            try {
                ndef.close()
            } catch (e: Exception) {
                Log.e(TAG, "关闭 NDEF 连接失败", e)
            }
        }
    }
    
    /**
     * 格式化并写入标签
     */
    private fun formatAndWriteTag(ndefFormatable: NdefFormatable, data: String): Boolean {
        return try {
            ndefFormatable.connect()
            
            // 创建 NDEF 消息
            val mimeBytes = "text/plain".toByteArray(StandardCharsets.US_ASCII)
            val payload = data.toByteArray(StandardCharsets.UTF_8)
            val record = android.nfc.NdefRecord(
                android.nfc.NdefRecord.TNF_MIME_MEDIA,
                mimeBytes,
                ByteArray(0),
                payload
            )
            
            val message = android.nfc.NdefMessage(arrayOf(record))
            
            // 格式化并写入
            ndefFormatable.format(message)
            
            Log.d(TAG, "标签格式化并写入成功")
            true
        } catch (e: Exception) {
            Log.e(TAG, "格式化标签失败", e)
            false
        } finally {
            try {
                ndefFormatable.close()
            } catch (e: Exception) {
                Log.e(TAG, "关闭 NdefFormatable 连接失败", e)
            }
        }
    }
    
    /**
     * 将字节数组转换为十六进制字符串
     */
    private fun bytesToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02X".format(it) }
    }
}
