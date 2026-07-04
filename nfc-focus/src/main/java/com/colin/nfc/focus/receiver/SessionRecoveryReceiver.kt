package com.colin.nfc.focus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.colin.nfc.focus.data.local.NfcFocusDatabase
import com.colin.nfc.focus.service.FocusBlockingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 会话恢复广播接收器
 * 
 * 监听以下系统事件：
 * 1. BOOT_COMPLETED - 设备开机完成
 * 2. MY_PACKAGE_REPLACED - 应用被更新（覆盖安装）
 * 
 * 功能：
 * - 检测到活跃会话时自动恢复前台服务
 * - 确保用户不会因为设备重启而丢失专注状态
 */
class SessionRecoveryReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "SessionRecoveryReceiver"
    }
    
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        
        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "设备开机完成，检查是否需要恢复会话")
                handleSessionRecovery(context)
            }
            
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d(TAG, "应用被更新，检查是否需要恢复会话")
                handleSessionRecovery(context)
            }
            
            else -> {
                Log.w(TAG, "未知广播: $action")
            }
        }
    }
    
    /**
     * 处理会话恢复
     */
    private fun handleSessionRecovery(context: Context) {
        receiverScope.launch {
            try {
                // 1. 查询数据库中的活跃会话
                val database = NfcFocusDatabase.getDatabase(context)
                val activeSession = database.focusSessionDao().getActiveSession()
                
                if (activeSession == null) {
                    Log.d(TAG, "没有活跃会话，无需恢复")
                    return@launch
                }
                
                Log.i(TAG, "发现活跃会话，准备恢复: ${activeSession.id}")
                
                // 2. 检查会话是否超时
                if (activeSession.isExpired()) {
                    Log.w(TAG, "会话已超时，自动结束")
                    database.focusSessionDao().endSession(
                        sessionId = activeSession.id,
                        endTime = System.currentTimeMillis(),
                        completedNormally = false
                    )
                    return@launch
                }
                
                // 3. 检查服务是否已经在运行
                if (FocusBlockingService.isServiceStarted) {
                    Log.d(TAG, "服务已在运行，无需恢复")
                    return@launch
                }
                
                // 4. 启动前台服务
                FocusBlockingService.startService(
                    context = context,
                    sessionId = activeSession.id,
                    sceneId = activeSession.sceneId,
                    sceneName = activeSession.sceneName,
                    configJson = activeSession.blockingConfigJson
                )
                
                Log.i(TAG, "会话恢复成功: ${activeSession.id}")
                
            } catch (e: Exception) {
                Log.e(TAG, "会话恢复失败", e)
            }
        }
    }
}
