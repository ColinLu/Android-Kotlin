package com.colin.nfc.focus.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.colin.nfc.focus.data.local.NfcFocusDatabase
import com.colin.nfc.focus.service.FocusBlockingService

/**
 * 专注会话保活 Worker
 * 
 * 功能：
 * 1. 每 15 分钟检查一次是否有活跃的专注会话
 * 2. 如果有活跃会话但服务未运行，自动重启服务
 * 3. 确保专注会话不会因系统资源紧张而被中断
 * 
 * 调度策略：
 * - 定期执行：每 15 分钟
 * - 失败重试：指数退避（最短 5 分钟，最长 30 分钟）
 */
class FocusKeepAliveWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    companion object {
        private const val TAG = "FocusKeepAliveWorker"
        
        /**
         * Worker 唯一标识
         */
        const val WORK_NAME = "focus_keep_alive_worker"
        
        /**
         * 检查间隔（分钟）
         */
        const val CHECK_INTERVAL_MINUTES = 15L
    }
    
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "开始执行保活检查")
            
            // 1. 检查是否有活跃的专注会话
            val database = NfcFocusDatabase.getDatabase(applicationContext)
            val activeSession = database.focusSessionDao().getActiveSession()
            
            if (activeSession == null) {
                Log.d(TAG, "没有活跃会话，跳过保活检查")
                return Result.success()
            }
            
            Log.d(TAG, "检测到活跃会话: ${activeSession.id}")
            
            // 2. 检查会话是否超时
            if (activeSession.isExpired()) {
                Log.w(TAG, "会话已超时，自动结束")
                database.focusSessionDao().endSession(
                    sessionId = activeSession.id,
                    endTime = System.currentTimeMillis(),
                    completedNormally = false
                )
                return Result.success()
            }
            
            // 3. 检查前台服务是否正在运行
            val isServiceRunning = FocusBlockingService.isServiceStarted
            
            if (!isServiceRunning) {
                Log.w(TAG, "服务未运行，尝试重启服务")
                
                // 重启前台服务
                FocusBlockingService.startService(
                    context = applicationContext,
                    sessionId = activeSession.id,
                    sceneId = activeSession.sceneId,
                    sceneName = activeSession.sceneName,
                    configJson = activeSession.blockingConfigJson
                )
                
                Log.i(TAG, "服务重启成功")
            } else {
                Log.d(TAG, "服务正常运行")
            }
            
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "保活检查失败", e)
            
            // 根据重试策略决定是否重试
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
