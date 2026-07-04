package com.colin.nfc.focus.blocker

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.colin.nfc.focus.service.FocusAccessibilityService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 无障碍拦截策略
 * 
 * 这是实现"严格模式"强制拦截的核心策略
 * 
 * 特性：
 * - 监听所有应用切换事件
 * - 检测到分心应用时立即拦截
 * - 服务被用户关闭后，提供引导重新开启
 * 
 * 注意：
 * - 需要用户在系统设置中手动授权
 * - 授权路径：设置 > 无障碍 > 已下载的应用 > 专注模式
 */
class AccessibilityStrategy : AppBlockingStrategy {
    
    companion object {
        private const val TAG = "AccessibilityStrategy"
        
        /**
         * 无障碍服务的 ComponentName
         */
        private const val SERVICE_COMPONENT = 
            "com.colin.nfc.focus.service.FocusAccessibilityService"
    }
    
    override val type: BlockingStrategyType = BlockingStrategyType.ACCESSIBILITY
    
    private var context: Context? = null
    private var isMonitoring = false
    
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override suspend fun initialize(context: Context): Result<Unit> {
        this.context = context.applicationContext
        Log.d(TAG, "初始化无障碍策略")
        
        // 检查服务是否正在运行
        if (!isServiceEnabled(context)) {
            Log.w(TAG, "无障碍服务未启用，需要引导用户授权")
            return Result.failure(
                SecurityException("无障碍服务未启用，请引导用户授权")
            )
        }
        
        return Result.success(Unit)
    }
    
    override fun startMonitoring(config: BlockingConfig): Result<Unit> {
        val ctx = context ?: return Result.failure(IllegalStateException("未初始化"))
        
        // 更新分心应用列表
        FocusAccessibilityService.blockedApps = config.blockedApps.toSet()
        
        // 设置拦截回调
        FocusAccessibilityService.onAppBlocked = { packageName ->
            Log.w(TAG, "拦截到分心应用: $packageName")
            // TODO: 触发覆盖窗口显示（在第二阶段后续实现）
        }
        
        isMonitoring = true
        Log.d(TAG, "开始无障碍监控，blockedApps: ${config.blockedApps.size}")
        
        return Result.success(Unit)
    }
    
    override fun stopMonitoring(): Result<Unit> {
        // 清除回调
        FocusAccessibilityService.onAppBlocked = null
        FocusAccessibilityService.blockedApps = emptySet()
        isMonitoring = false
        
        Log.d(TAG, "停止无障碍监控")
        return Result.success(Unit)
    }
    
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    override fun isPermissionGranted(context: Context): Boolean {
        return isServiceEnabled(context)
    }
    
    @RequiresApi(Build.VERSION_CODES.ECLAIR)
    override suspend fun requestPermission(activity: Activity): Result<Unit> {
        return withContext(Dispatchers.Main) {
            try {
                // 打开无障碍设置页面
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent)
                
                Log.d(TAG, "已打开无障碍设置页面，等待用户授权")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "打开无障碍设置失败", e)
                Result.failure(e)
            }
        }
    }
    
    override suspend fun addBlockedApp(packageName: String): Result<Unit> {
        val currentApps = FocusAccessibilityService.blockedApps.toMutableSet()
        currentApps.add(packageName)
        FocusAccessibilityService.blockedApps = currentApps
        
        Log.d(TAG, "添加分心应用: $packageName")
        return Result.success(Unit)
    }
    
    override suspend fun removeBlockedApp(packageName: String): Result<Unit> {
        val currentApps = FocusAccessibilityService.blockedApps.toMutableSet()
        currentApps.remove(packageName)
        FocusAccessibilityService.blockedApps = currentApps
        
        Log.d(TAG, "移除分心应用: $packageName")
        return Result.success(Unit)
    }
    
    /**
     * 检查无障碍服务是否已启用
     */
    @RequiresApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private fun isServiceEnabled(context: Context): Boolean {
        return try {
            val accessibilityManager = context.getSystemService(
                Context.ACCESSIBILITY_SERVICE
            ) as android.view.accessibility.AccessibilityManager
            
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_GENERIC
            )
            
            enabledServices.any {
                it.id.contains(SERVICE_COMPONENT, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "检查无障碍服务状态失败", e)
            false
        }
    }
    
    /**
     * 获取无障碍服务授权引导信息
     * 
     * @return 包含标题、描述和步骤的引导信息
     */
    fun getAuthorizationGuide(): AccessibilityAuthGuide {
        return AccessibilityAuthGuide(
            title = "开启无障碍服务",
            description = "专注模式需要使用无障碍服务来检测并拦截分心应用",
            steps = listOf(
                "1. 点击「去设置」按钮",
                "2. 找到「专注模式」或「FocusAccessibilityService」",
                "3. 开启开关",
                "4. 在确认对话框中点击「允许」",
                "",
                "💡 提示：",
                "- 此服务仅在专注会话期间运行",
                "- 不会收集任何个人信息",
                "- 可随时在设置中关闭"
            ),
            warning = "⚠️ 如果用户强制关闭服务，专注功能将无法正常工作"
        )
    }
}

/**
 * 无障碍服务授权引导信息
 */
data class AccessibilityAuthGuide(
    val title: String,
    val description: String,
    val steps: List<String>,
    val warning: String
)
