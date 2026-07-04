package com.colin.nfc.focus.blocker

/**
 * 屏蔽策略类型枚举
 */
enum class BlockingStrategyType {
    /**
     * 使用情况统计监控（基础监控）
     */
    USAGE_STATS,
    
    /**
     * 覆盖窗口提醒（UI 提醒）
     */
    OVERLAY_ALERT,
    
    /**
     * 无障碍服务拦截（强制阻止）
     */
    ACCESSIBILITY,
    
    /**
     * 设备管理员控制（企业级，可选）
     */
    DEVICE_POLICY
}

/**
 * 专注模式枚举
 */
enum class FocusMode {
    /**
     * 温和模式：仅监控和提醒，不强制阻止
     */
    GENTLE,
    
    /**
     * 平衡模式：监控 + 提醒 + 轻度拦截
     */
    BALANCED,
    
    /**
     * 严格模式：所有策略启用，强制阻止分心应用
     */
    STRICT
}

/**
 * 提醒方式枚举
 */
enum class AlertType {
    /**
     * 全屏覆盖对话框
     */
    OVERLAY_DIALOG,
    
    /**
     * 通知栏提醒
     */
    NOTIFICATION,
    
    /**
     * Toast 提示
     */
    TOAST,
    
    /**
     * 震动提醒
     */
    VIBRATION
}

/**
 * 屏蔽配置数据类
 * 
 * @param blockedApps 被屏蔽的应用包名列表
 * @param focusMode 专注模式（决定使用哪些策略）
 * @param alertType 提醒方式
 * @param checkInterval 检测间隔（毫秒），默认 1 秒
 * @param allowTemporaryExit 是否允许临时退出
 * @param exitPassword 退出密码（可选，用于防止随意退出）
 */
data class BlockingConfig(
    val blockedApps: List<String> = emptyList(),
    val focusMode: FocusMode = FocusMode.GENTLE,
    val alertType: AlertType = AlertType.OVERLAY_DIALOG,
    val checkInterval: Long = 1000L,
    val allowTemporaryExit: Boolean = false,
    val exitPassword: String? = null
)

/**
 * 权限引导信息
 * 
 * @param title 权限标题
 * @param description 权限描述
 * @param actionText 操作按钮文本
 * @param intentAction 跳转到设置的 Intent Action
 */
data class PermissionGuide(
    val title: String,
    val description: String,
    val actionText: String,
    val intentAction: String?
)

/**
 * 分心事件
 * 
 * @param packageName 分心应用的包名
 * @param appName 应用名称
 * @param timestamp 发生时间戳
 * @param sessionId 当前专注会话 ID
 */
data class DistractionEvent(
    val packageName: String,
    val appName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sessionId: Long? = null
)
