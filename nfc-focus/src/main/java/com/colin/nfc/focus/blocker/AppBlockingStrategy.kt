package com.colin.nfc.focus.blocker

/**
 * 应用屏蔽策略接口
 * 
 * 所有具体的屏蔽策略都需要实现此接口，以便统一管理
 */
interface AppBlockingStrategy {
    
    /**
     * 策略类型标识
     */
    val type: BlockingStrategyType
    
    /**
     * 初始化策略
     * @param context 上下文
     * @return 初始化结果
     */
    suspend fun initialize(context: android.content.Context): Result<Unit>
    
    /**
     * 启动监控
     * @param config 屏蔽配置
     * @return 启动结果
     */
    fun startMonitoring(config: BlockingConfig): Result<Unit>
    
    /**
     * 停止监控
     * @return 停止结果
     */
    fun stopMonitoring(): Result<Unit>
    
    /**
     * 检查权限是否已授予
     * @param context 上下文
     * @return 是否已授权
     */
    fun isPermissionGranted(context: android.content.Context): Boolean
    
    /**
     * 请求必要权限
     * @param activity 当前 Activity
     * @return 请求结果
     */
    suspend fun requestPermission(activity: android.app.Activity): Result<Unit>
    
    /**
     * 添加被屏蔽的应用
     * @param packageName 应用包名
     * @return 操作结果
     */
    suspend fun addBlockedApp(packageName: String): Result<Unit>
    
    /**
     * 移除被屏蔽的应用
     * @param packageName 应用包名
     * @return 操作结果
     */
    suspend fun removeBlockedApp(packageName: String): Result<Unit>
}
