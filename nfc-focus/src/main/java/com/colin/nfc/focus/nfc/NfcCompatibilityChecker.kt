package com.colin.nfc.focus.nfc

import android.content.Context
import android.nfc.NfcAdapter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * NFC 兼容性检测器
 * 
 * 用于检测不同厂商设备的 NFC 支持情况，并提供相应的适配方案。
 * 特别针对国内主流手机厂商（华为、小米、OPPO、vivo、荣耀等）进行优化。
 */
@RequiresApi(Build.VERSION_CODES.GINGERBREAD_MR1)
class NfcCompatibilityChecker(private val context: Context) {
    
    companion object {
        private const val TAG = "NfcCompatibilityChecker"
    }
    
    /**
     * 检查 NFC 支持状态
     * 
     * @return NFC 支持结果
     */
    fun checkNfcSupport(): NfcSupportResult {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        
        // 1. 检查硬件支持
        if (nfcAdapter == null) {
            Log.w(TAG, "设备不支持 NFC 硬件")
            return NfcSupportResult.NoHardware
        }
        
        // 2. 检查 NFC 是否启用
        if (!nfcAdapter.isEnabled) {
            Log.w(TAG, "NFC 功能未启用")
            return NfcSupportResult.Disabled
        }
        
        // 3. 检查厂商特定限制
        val manufacturer = Build.MANUFACTURER.lowercase()
        
        return when {
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> {
                checkHuaweiNfcSupport()
            }
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> {
                checkXiaomiNfcSupport()
            }
            manufacturer.contains("oppo") || manufacturer.contains("oneplus") -> {
                checkOppoNfcSupport()
            }
            manufacturer.contains("vivo") -> {
                checkVivoNfcSupport()
            }
            manufacturer.contains("samsung") -> {
                checkSamsungNfcSupport()
            }
            else -> {
                NfcSupportResult.Supported
            }
        }
    }
    
    /**
     * 检查华为/荣耀设备 NFC 支持
     */
    private fun checkHuaweiNfcSupport(): NfcSupportResult {
        // 华为设备可能需要特殊权限
        return NfcSupportResult.SupportedWithWarning(
            message = "华为设备请确保在设置中开启 NFC 并允许前台读取",
            requiresSpecialPermission = true
        )
    }
    
    /**
     * 检查小米设备 NFC 支持
     */
    private fun checkXiaomiNfcSupport(): NfcSupportResult {
        // MIUI 可能限制某些应用使用 NFC
        return NfcSupportResult.SupportedWithWarning(
            message = "小米设备请在「设置 > 更多设置 > NFC」中将本应用加入白名单",
            requiresSpecialPermission = true
        )
    }
    
    /**
     * 检查 OPPO/一加设备 NFC 支持
     */
    private fun checkOppoNfcSupport(): NfcSupportResult {
        return NfcSupportResult.SupportedWithWarning(
            message = "OPPO/一加设备请确保 NFC 已开启且屏幕解锁",
            requiresSpecialPermission = false
        )
    }
    
    /**
     * 检查 vivo 设备 NFC 支持
     */
    private fun checkVivoNfcSupport(): NfcSupportResult {
        return NfcSupportResult.Supported
    }
    
    /**
     * 检查三星设备 NFC 支持
     */
    private fun checkSamsungNfcSupport(): NfcSupportResult {
        return NfcSupportResult.Supported
    }
    
    /**
     * 获取 NFC 开启引导信息
     * 
     * @return 引导信息（标题、描述、操作步骤）
     */
    fun getNfcEnableGuide(): NfcEnableGuide {
        val manufacturer = Build.MANUFACTURER.lowercase()
        
        return when {
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> {
                NfcEnableGuide(
                    title = "开启华为 NFC",
                    description = "华为设备需要手动开启 NFC 功能",
                    steps = listOf(
                        "1. 打开「设置」",
                        "2. 点击「更多连接」或「无线和网络」",
                        "3. 找到并开启「NFC」开关",
                        "4. 建议同时开启「Android Beam」（如果可用）"
                    )
                )
            }
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> {
                NfcEnableGuide(
                    title = "开启小米 NFC",
                    description = "MIUI 系统需要开启 NFC 并将应用加入白名单",
                    steps = listOf(
                        "1. 打开「设置」",
                        "2. 点击「更多设置」",
                        "3. 找到并开启「NFC」",
                        "4. 点击「NFC 默认应用」，选择本应用",
                        "5. 返回「更多设置」，将本应用加入 NFC 白名单"
                    )
                )
            }
            manufacturer.contains("oppo") || manufacturer.contains("oneplus") -> {
                NfcEnableGuide(
                    title = "开启 OPPO NFC",
                    description = "ColorOS 系统需要开启 NFC 功能",
                    steps = listOf(
                        "1. 打开「设置」",
                        "2. 点击「其他无线连接」或「连接与共享」",
                        "3. 找到并开启「NFC」开关",
                        "4. 确保屏幕处于解锁状态"
                    )
                )
            }
            manufacturer.contains("vivo") -> {
                NfcEnableGuide(
                    title = "开启 vivo NFC",
                    description = "Funtouch OS 系统需要开启 NFC 功能",
                    steps = listOf(
                        "1. 打开「设置」",
                        "2. 点击「其他网络与连接」",
                        "3. 找到并开启「NFC」开关"
                    )
                )
            }
            manufacturer.contains("samsung") -> {
                NfcEnableGuide(
                    title = "开启三星 NFC",
                    description = "One UI 系统需要开启 NFC 功能",
                    steps = listOf(
                        "1. 打开「设置」",
                        "2. 点击「连接」",
                        "3. 找到并开启「NFC 和非接触式支付」"
                    )
                )
            }
            else -> {
                NfcEnableGuide(
                    title = "开启 NFC",
                    description = "请在系统设置中开启 NFC 功能",
                    steps = listOf(
                        "1. 打开「设置」",
                        "2. 找到「连接」或「无线和网络」",
                        "3. 开启「NFC」开关"
                    )
                )
            }
        }
    }
}

/**
 * NFC 支持结果
 */
sealed class NfcSupportResult {
    /**
     * 完全支持
     */
    data object Supported : NfcSupportResult()
    
    /**
     * 支持但有警告（需要特殊配置）
     */
    data class SupportedWithWarning(
        val message: String,
        val requiresSpecialPermission: Boolean = false
    ) : NfcSupportResult()
    
    /**
     * NFC 未启用
     */
    data object Disabled : NfcSupportResult()
    
    /**
     * 无 NFC 硬件
     */
    data object NoHardware : NfcSupportResult()
}

/**
 * NFC 开启引导信息
 */
data class NfcEnableGuide(
    val title: String,
    val description: String,
    val steps: List<String>
)
