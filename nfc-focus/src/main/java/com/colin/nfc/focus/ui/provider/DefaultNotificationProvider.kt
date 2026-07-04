package com.colin.nfc.focus.ui.provider

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.colin.nfc.focus.R

/**
 * 默认通知提供者实现
 * 
 * 使用 Material Design 3 风格，支持深色模式
 * 
 * 通知渠道：
 * - FOCUS_SESSION: 专注进行中（持续显示）
 * - FOCUS_ALERT: 分心提醒（重要，可震动）
 * - FOCUS_COMPLETED: 专注完成（一次性）
 */
class DefaultNotificationProvider : INotificationProvider {
    
    companion object {
        private const val CHANNEL_SESSION = "focus_session"
        private const val CHANNEL_ALERT = "focus_alert"
        private const val CHANNEL_COMPLETED = "focus_completed"
        
        const val NOTIFICATION_ID_SESSION = 1001
        const val NOTIFICATION_ID_ALERT = 1002
    }
    
    override fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 专注会话渠道
        val sessionChannel = NotificationChannel(
            CHANNEL_SESSION,
            "专注会话",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "显示当前专注会话状态"
            setShowBadge(false)
            enableVibration(false)
            enableLights(false)
        }
        
        // 分心提醒渠道
        val alertChannel = NotificationChannel(
            CHANNEL_ALERT,
            "分心提醒",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "检测到分心时发送提醒"
            setShowBadge(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
            enableLights(true)
        }
        
        // 专注完成渠道
        val completedChannel = NotificationChannel(
            CHANNEL_COMPLETED,
            "专注完成",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "专注会话完成时发送通知"
            setShowBadge(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 200, 100, 200)
            enableLights(true)
        }
        
        // 注册渠道
        notificationManager.createNotificationChannel(sessionChannel)
        notificationManager.createNotificationChannel(alertChannel)
        notificationManager.createNotificationChannel(completedChannel)
    }
    
    override fun showDistractionAlert(context: Context, alertData: AlertData) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERT)
            .setSmallIcon(R.drawable.ic_notification) // TODO: 需要添加图标资源
            .setContentTitle("⚠️ 检测到分心")
            .setContentText("您正在使用 ${alertData.appName}，建议返回专注")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_ALERT, notification)
    }
    
    override fun showSessionNotification(context: Context, sessionData: SessionData) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 创建点击通知的 PendingIntent（可选：跳转到结束会话界面）
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(), // TODO: 添加目标 Activity
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_SESSION)
            .setSmallIcon(R.drawable.ic_notification) // TODO: 需要添加图标资源
            .setContentTitle("🎯 专注中")
            .setContentText("${sessionData.sceneName} - 已坚持 ${formatDuration(sessionData.startTime)}")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // 设置为持续通知，不可滑动删除
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_SESSION, notification)
    }
    
    override fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
    
    /**
     * 格式化持续时间
     */
    private fun formatDuration(startTime: Long): String {
        val elapsedMillis = System.currentTimeMillis() - startTime
        val elapsedSeconds = elapsedMillis / 1000
        
        val minutes = elapsedSeconds / 60
        val hours = minutes / 60
        
        return if (hours > 0) {
            "${hours}小时${minutes % 60}分钟"
        } else {
            "${minutes}分钟"
        }
    }
}
