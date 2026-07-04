package com.colin.nfc.focus.blocker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log

/**
 * 应用屏蔽策略管理器
 * 
 * 负责管理和协调多个屏蔽策略，根据专注模式配置动态启用/禁用策略组合。
 * 
 * 使用示例：
 * ```kotlin
 * val manager = AppBlockerManager(context)
 * 
 * // 注册策略
 * manager.registerStrategy(UsageStatsStrategy(context))
 * manager.registerStrategy(OverlayAlertStrategy(context))
 * 
 * // 启动屏蔽
 * val config = BlockingConfig(
 *     blockedApps = listOf("com.ss.android.ugc.aweme"),
 *     focusMode = FocusMode.GENTLE
 * )
 * manager.startBlocking(config)
 * 
 * // 停止屏蔽
 * manager.stopBlocking()
 * ```
 */
class AppBlockerManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AppBlockerManager"
    }
    
    // 所有已注册的策略
    private val strategies = mutableMapOf<BlockingStrategyType, AppBlockingStrategy>()
    
    // 当前激活的策略
    private val activeStrategies = mutableListOf<AppBlockingStrategy>()
    
    // 当前配置
    private var currentConfig: BlockingConfig? = null
    
    // 分心事件监听器
    private var distractionListener: ((DistractionEvent) -> Unit)? = null
    
    init {
        Log.d(TAG, "AppBlockerManager 初始化")
    }
    
    /**
     * 注册屏蔽策略
     * 
     * @param strategy 要注册的策略实例
     */
    fun registerStrategy(strategy: AppBlockingStrategy) {
        strategies[strategy.type] = strategy
        Log.d(TAG, "注册策略: ${strategy.type}")
    }
    
    /**
     * 获取已注册的策略
     * 
     * @param type 策略类型
     * @return 策略实例或 null
     */
    fun getStrategy(type: BlockingStrategyType): AppBlockingStrategy? {
        return strategies[type]
    }
    
    /**
     * 根据专注模式选择策略组合
     * 
     * @param mode 专注模式
     * @return 应该启用的策略列表
     */
    private fun selectStrategiesForMode(mode: FocusMode): List<AppBlockingStrategy> {
        return when (mode) {
            FocusMode.GENTLE -> {
                // 温和模式：仅监控 + 提醒
                listOfNotNull(
                    strategies[BlockingStrategyType.USAGE_STATS],
                    strategies[BlockingStrategyType.OVERLAY_ALERT]
                )
            }
            FocusMode.BALANCED -> {
                // 平衡模式：监控 + 提醒 + 轻度拦截
                listOfNotNull(
                    strategies[BlockingStrategyType.USAGE_STATS],
                    strategies[BlockingStrategyType.OVERLAY_ALERT],
                    strategies[BlockingStrategyType.ACCESSIBILITY]
                )
            }
            FocusMode.STRICT -> {
                // 严格模式：所有可用策略
                listOfNotNull(
                    strategies[BlockingStrategyType.USAGE_STATS],
                    strategies[BlockingStrategyType.OVERLAY_ALERT],
                    strategies[BlockingStrategyType.ACCESSIBILITY]
                    // DevicePolicy 可选
                )
            }
        }
    }
    
    /**
     * 启动屏蔽服务
     * 
     * @param config 屏蔽配置
     * @return 启动结果
     */
    suspend fun startBlocking(config: BlockingConfig): Result<Unit> {
        return try {
            Log.d(TAG, "启动屏蔽服务，模式: ${config.focusMode}")
            
            currentConfig = config
            
            // 1. 选择策略
            activeStrategies.clear()
            val selectedStrategies = selectStrategiesForMode(config.focusMode)
            activeStrategies.addAll(selectedStrategies)
            
            if (activeStrategies.isEmpty()) {
                return Result.failure(IllegalStateException("没有可用的屏蔽策略"))
            }
            
            Log.d(TAG, "选中的策略: ${activeStrategies.map { it.type }}")
            
            // 2. 检查所有策略的权限
            for (strategy in activeStrategies) {
                if (!strategy.isPermissionGranted(context)) {
                    val errorMsg = "策略 ${strategy.type} 权限未授予"
                    Log.e(TAG, errorMsg)
                    stopBlocking() // 回滚
                    return Result.failure(SecurityException(errorMsg))
                }
            }
            
            // 3. 初始化并启动所有策略
            for (strategy in activeStrategies) {
                strategy.initialize(context).onFailure {
                    Log.e(TAG, "策略 ${strategy.type} 初始化失败", it)
                    stopBlocking() // 回滚
                    return Result.failure(it)
                }
                
                strategy.startMonitoring(config).onFailure {
                    Log.e(TAG, "策略 ${strategy.type} 启动失败", it)
                    stopBlocking() // 回滚
                    return Result.failure(it)
                }
            }
            
            Log.d(TAG, "屏蔽服务启动成功，激活 ${activeStrategies.size} 个策略")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "启动屏蔽服务失败", e)
            stopBlocking() // 确保清理
            Result.failure(e)
        }
    }
    
    /**
     * 停止屏蔽服务
     * 
     * @return 停止结果
     */
    suspend fun stopBlocking(): Result<Unit> {
        return try {
            Log.d(TAG, "停止屏蔽服务")
            
            var lastError: Exception? = null
            
            // 停止所有激活的策略
            for (strategy in activeStrategies) {
                try {
                    strategy.stopMonitoring().onFailure {
                        Log.e(TAG, "策略 ${strategy.type} 停止失败", it)
                        lastError = it as? Exception ?: Exception(it.message)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "策略 ${strategy.type} 停止异常", e)
                    lastError = e
                }
            }
            
            activeStrategies.clear()
            currentConfig = null
            
            if (lastError != null) {
                Result.failure(lastError!!)
            } else {
                Log.d(TAG, "屏蔽服务已停止")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "停止屏蔽服务失败", e)
            Result.failure(e)
        }
    }
    
    /**
     * 检查所有策略的权限状态
     * 
     * @return 每个策略的权限状态映射
     */
    fun checkAllPermissions(): Map<BlockingStrategyType, Boolean> {
        return strategies.mapValues { (_, strategy) ->
            strategy.isPermissionGranted(context)
        }
    }
    
    /**
     * 检查是否有监控权限（至少一个策略可用）
     * 
     * @return 是否有可用权限
     */
    fun hasAnyPermission(): Boolean {
        return strategies.any { (_, strategy) ->
            strategy.isPermissionGranted(context)
        }
    }
    
    /**
     * 获取权限引导信息
     * 
     * @param type 策略类型
     * @return 权限引导信息
     */
    fun getPermissionGuide(type: BlockingStrategyType): PermissionGuide {
        return when (type) {
            BlockingStrategyType.USAGE_STATS -> {
                PermissionGuide(
                    title = "使用情况访问权限",
                    description = "需要此权限来监控当前运行的应用，以便检测分心行为",
                    actionText = "前往设置",
                    intentAction = Settings.ACTION_USAGE_ACCESS_SETTINGS
                )
            }
            BlockingStrategyType.ACCESSIBILITY -> {
                PermissionGuide(
                    title = "无障碍服务权限",
                    description = "需要此权限来拦截分心应用，提供强制专注功能",
                    actionText = "开启无障碍",
                    intentAction = Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            }
            BlockingStrategyType.OVERLAY_ALERT -> {
                PermissionGuide(
                    title = "悬浮窗权限",
                    description = "需要此权限来显示专注提醒对话框",
                    actionText = "开启悬浮窗",
                    intentAction = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                )
            }
            BlockingStrategyType.DEVICE_POLICY -> {
                PermissionGuide(
                    title = "设备管理员权限",
                    description = "需要此权限进行系统级应用控制（企业级功能）",
                    actionText = "激活设备管理员",
                    intentAction = null // 需要特殊处理
                )
            }
        }
    }
    
    /**
     * 跳转到权限设置页面
     * 
     * @param activity 当前 Activity
     * @param type 策略类型
     */
    fun navigateToPermissionSettings(activity: Activity, type: BlockingStrategyType) {
        val guide = getPermissionGuide(type)
        guide.intentAction?.let { action ->
            try {
                val intent = Intent(action)
                if (action == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                    intent.data = android.net.Uri.parse("package:${context.packageName}")
                }
                activity.startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, "跳转权限设置失败", e)
            }
        }
    }
    
    /**
     * 设置分心事件监听器
     * 
     * @param listener 监听器回调
     */
    fun setDistractionListener(listener: ((DistractionEvent) -> Unit)?) {
        this.distractionListener = listener
    }
    
    /**
     * 通知分心事件（由策略内部调用）
     * 
     * @param event 分心事件
     */
    internal fun notifyDistraction(event: DistractionEvent) {
        distractionListener?.invoke(event)
        Log.w(TAG, "检测到分心: ${event.appName} (${event.packageName})")
    }
    
    /**
     * 获取当前配置
     */
    fun getCurrentConfig(): BlockingConfig? {
        return currentConfig
    }
    
    /**
     * 获取当前激活的策略数量
     */
    fun getActiveStrategyCount(): Int {
        return activeStrategies.size
    }
}
