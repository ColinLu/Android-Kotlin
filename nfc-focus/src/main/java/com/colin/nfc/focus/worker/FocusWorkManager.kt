package com.colin.nfc.focus.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * WorkManager 管理器
 * 
 * 负责调度和取消保活任务
 */
object FocusWorkManager {
    
    private const val TAG = "FocusWorkManager"
    
    /**
     * 启动保活任务
     * 
     * 调度策略：
     * - 执行间隔：15 分钟
     * - 网络要求：无（不需要网络）
     * - 失败重试：指数退避
     * 
     * @param context 上下文
     */
    fun startKeepAliveWorker(context: Context) {
        try {
            val workManager = WorkManager.getInstance(context)
            
            // 创建工作请求
            val workRequest = PeriodicWorkRequestBuilder<FocusKeepAliveWorker>(
                repeatInterval = FocusKeepAliveWorker.CHECK_INTERVAL_MINUTES,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).apply {
                // 设置约束条件（无需网络）
                setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                
                // 设置重试策略
                setInitialDelay(5, TimeUnit.MINUTES) // 首次延迟 5 分钟执行
            }.build()
            
            //  enqueue 定期任务
            workManager.enqueueUniquePeriodicWork(
                FocusKeepAliveWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // 如果已存在则保留
                workRequest
            )
            
            Log.d(TAG, "保活任务已启动，检查间隔: ${FocusKeepAliveWorker.CHECK_INTERVAL_MINUTES} 分钟")
        } catch (e: Exception) {
            Log.e(TAG, "启动保活任务失败", e)
        }
    }
    
    /**
     * 停止保活任务
     * 
     * @param context 上下文
     */
    fun stopKeepAliveWorker(context: Context) {
        try {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(FocusKeepAliveWorker.WORK_NAME)
            
            Log.d(TAG, "保活任务已停止")
        } catch (e: Exception) {
            Log.e(TAG, "停止保活任务失败", e)
        }
    }
    
    /**
     * 检查保活任务是否正在运行
     * 
     * @param context 上下文
     * @return 是否正在运行
     */
    fun isKeepAliveWorkerRunning(context: Context): Boolean {
        return try {
            val workManager = WorkManager.getInstance(context)
            val workInfos = workManager.getWorkInfosForUniqueWork(FocusKeepAliveWorker.WORK_NAME).get()
            
            workInfos.any { !it.state.isFinished }
        } catch (e: Exception) {
            Log.e(TAG, "检查工作状态失败", e)
            false
        }
    }
}
