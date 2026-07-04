package com.colin.nfc.focus.ui.provider

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.colin.nfc.focus.blocker.FocusMode

/**
 * 覆盖窗口提供者接口
 * 
 * 集成方可以实现此接口来自定义分心提醒的覆盖窗口 UI
 * 
 * 使用示例：
 * ```kotlin
 * class CustomAlertViewProvider : IAlertViewProvider {
 *     override fun createOverlayView(context: Context): View {
 *         return LayoutInflater.from(context).inflate(R.layout.my_custom_alert, null)
 *     }
 *     
 *     override fun bindData(view: View, alertData: AlertData) {
 *         // 自定义数据绑定
 *     }
 *     
 *     override fun setActionListener(view: View, listener: AlertActionListener) {
 *         // 自定义按钮事件
 *     }
 * }
 * 
 * NFCFocus.getInstance().getManager().setAlertViewProvider(CustomAlertViewProvider())
 * ```
 */
interface IAlertViewProvider {
    
    /**
     * 创建覆盖窗口视图
     * @param context 上下文
     * @return 覆盖窗口的根视图
     */
    fun createOverlayView(context: Context): View
    
    /**
     * 绑定提醒数据到视图
     * @param view 覆盖窗口视图
     * @param alertData 提醒数据
     */
    fun bindData(view: View, alertData: AlertData)
    
    /**
     * 设置操作监听器
     * @param view 覆盖窗口视图
     * @param listener 操作回调
     */
    fun setActionListener(view: View, listener: AlertActionListener)
}

/**
 * 通知提供者接口
 * 
 * 集成方可以实现此接口来自定义通知样式和行为
 * 
 * 使用示例：
 * ```kotlin
 * class CustomNotificationProvider : INotificationProvider {
 *     override fun showDistractionAlert(context: Context, alertData: AlertData) {
 *         // 自定义通知实现
 *     }
 * }
 * 
 * NFCFocus.getInstance().getManager().setNotificationProvider(CustomNotificationProvider())
 * ```
 */
interface INotificationProvider {
    
    /**
     * 创建通知渠道（Android 8.0+）
     * @param context 上下文
     */
    fun createNotificationChannels(context: Context)
    
    /**
     * 显示分心提醒通知
     * @param context 上下文
     * @param alertData 提醒数据
     */
    fun showDistractionAlert(context: Context, alertData: AlertData)
    
    /**
     * 显示专注会话通知
     * @param context 上下文
     * @param sessionData 会话数据
     */
    fun showSessionNotification(context: Context, sessionData: SessionData)
    
    /**
     * 取消所有通知
     * @param context 上下文
     */
    fun cancelAllNotifications(context: Context)
}

/**
 * 提醒数据模型
 * 
 * @property packageName 分心应用的包名
 * @property appName 分心应用的名称
 * @property appIcon 分心应用的图标
 * @property focusMode 当前专注模式
 * @property allowDismiss 是否允许用户关闭提醒
 * @property timeoutSeconds 自动超时时间（秒），0 表示不超时
 * @property message 自定义提醒消息
 */
data class AlertData(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable? = null,
    val focusMode: FocusMode = FocusMode.GENTLE,
    val allowDismiss: Boolean = true,
    val timeoutSeconds: Int = 30,
    val message: String? = null
)

/**
 * 会话数据模型
 * 
 * @property sessionId 会话 ID
 * @property sceneName 场景名称
 * @property startTime 开始时间戳
 * @property durationMinutes 预计持续时长（分钟）
 * @property isPaused 是否暂停
 */
data class SessionData(
    val sessionId: Long,
    val sceneName: String,
    val startTime: Long,
    val durationMinutes: Int = 0,
    val isPaused: Boolean = false
)

/**
 * 提醒操作监听器
 * 
 * 当用户在覆盖窗口中执行操作时触发
 */
interface AlertActionListener {
    
    /**
     * 返回专注（关闭分心应用，回到上一个应用）
     */
    fun onReturnToFocus()
    
    /**
     * 关闭提醒（仅关闭覆盖窗口，不处理分心应用）
     */
    fun onDismissAlert()
    
    /**
     * 临时退出专注模式
     * @param minutes 退出时长（分钟），默认 5 分钟
     */
    fun onTemporaryExit(minutes: Int = 5)
}
