package com.colin.nfc.focus.blocker

import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import java.util.*

/**
 * 基于 UsageStatsManager 的应用监控策略
 * 
 * 通过定期查询前台应用使用情况，检测用户是否打开了被屏蔽的应用。
 * 这是最基础的监控策略，适用于所有专注模式。
 * 
 * 权限要求：
 * - PACKAGE_USAGE_STATS（需要用户手动在设置中授权）
 */
class UsageStatsStrategy(private val context: Context) : AppBlockingStrategy {
    
    companion object {
        private const val TAG = "UsageStatsStrategy"
    }
    
    override val type = BlockingStrategyType.USAGE_STATS
    
    private val usageStatsManager = context.getSystemService(
        Context.USAGE_STATS_SERVICE
    ) as? UsageStatsManager
    
    private var monitoringJob: Job? = null
    private var blockedApps = setOf<String>()
    private var currentConfig: BlockingConfig? = null
    
    // 用于避免重复报告同一个应用
    private var lastReportedApp: String? = null
    private var lastReportTime: Long = 0
    
    init {
        if (usageStatsManager == null) {
            Log.e(TAG, "UsageStatsManager 不可用")
        }
    }
    
    override suspend fun initialize(context: Context): Result<Unit> {
        return if (usageStatsManager != null) {
            Log.d(TAG, "UsageStatsStrategy 初始化成功")
            Result.success(Unit)
        } else {
            Log.e(TAG, "UsageStatsStrategy 初始化失败：服务不可用")
            Result.failure(IllegalStateException("UsageStatsManager 不可用"))
        }
    }
    
    override fun startMonitoring(config: BlockingConfig): Result<Unit> {
        if (usageStatsManager == null) {
            return Result.failure(IllegalStateException("UsageStatsManager 不可用"))
        }
        
        if (!isPermissionGranted(context)) {
            return Result.failure(SecurityException("未授予使用情况访问权限"))
        }
        
        currentConfig = config
        blockedApps = config.blockedApps.toSet()
        
        Log.d(TAG, "启动监控，屏蔽 ${blockedApps.size} 个应用，检测间隔: ${config.checkInterval}ms")
        
        // 启动协程定期检测
        monitoringJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val foregroundApp = getForegroundApp()
                    
                    if (foregroundApp != null && foregroundApp in blockedApps) {
                        // 检查是否需要报告（避免频繁重复报告）
                        val now = System.currentTimeMillis()
                        val shouldReport = if (foregroundApp == lastReportedApp) {
                            // 如果是同一个应用，至少间隔 5 秒再报告
                            (now - lastReportTime) > 5000
                        } else {
                            true
                        }
                        
                        if (shouldReport) {
                            val appName = getAppName(foregroundApp)
                            val event = DistractionEvent(
                                packageName = foregroundApp,
                                appName = appName,
                                sessionId = null // TODO: 从上层获取
                            )
                            
                            // 通知管理器
                            // 注意：这里需要通过某种方式访问 AppBlockerManager
                            // 暂时注释，后续通过回调或事件总线实现
                            // manager.notifyDistraction(event)
                            
                            lastReportedApp = foregroundApp
                            lastReportTime = now
                            
                            Log.w(TAG, "检测到分心应用: $appName ($foregroundApp)")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "检测前台应用失败", e)
                }
                
                delay(config.checkInterval)
            }
        }
        
        return Result.success(Unit)
    }
    
    override fun stopMonitoring(): Result<Unit> {
        monitoringJob?.cancel()
        monitoringJob = null
        blockedApps = emptySet()
        currentConfig = null
        lastReportedApp = null
        lastReportTime = 0
        
        Log.d(TAG, "停止监控")
        return Result.success(Unit)
    }
    
    override fun isPermissionGranted(context: Context): Boolean {
        return hasUsageStatsPermission(context)
    }
    
    override suspend fun requestPermission(activity: Activity): Result<Unit> {
        return try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            activity.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "跳转权限设置失败", e)
            Result.failure(e)
        }
    }
    
    override suspend fun addBlockedApp(packageName: String): Result<Unit> {
        blockedApps += packageName
        Log.d(TAG, "添加屏蔽应用: $packageName")
        return Result.success(Unit)
    }
    
    override suspend fun removeBlockedApp(packageName: String): Result<Unit> {
        blockedApps -= packageName
        Log.d(TAG, "移除屏蔽应用: $packageName")
        return Result.success(Unit)
    }
    
    /**
     * 获取当前前台应用包名
     */
    private fun getForegroundApp(): String? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 10000 // 查询最近 10 秒
        
        val stats = usageStatsManager?.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        
        if (stats.isNullOrEmpty()) {
            return null
        }
        
        // 找到最近使用且在前台的应用
        val foregroundApp = stats
            .filter { it.totalTimeInForeground > 0 }
            .filter { it.lastTimeUsed >= startTime }
            .maxByOrNull { it.lastTimeUsed }
        
        return foregroundApp?.packageName
    }
    
    /**
     * 检查是否有使用情况统计权限
     */
    private fun hasUsageStatsPermission(context: Context): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Log.e(TAG, "检查权限失败", e)
            false
        }
    }
    
    /**
     * 获取应用名称
     */
    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            // 如果无法获取应用名称，返回包名
            packageName
        }
    }
}
