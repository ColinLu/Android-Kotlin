package com.colin.nfc.focus.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.colin.nfc.focus.R
import com.colin.nfc.focus.blocker.AppBlockerManager
import com.colin.nfc.focus.blocker.BlockingConfig
import com.colin.nfc.focus.data.local.NfcFocusDatabase
import com.colin.nfc.focus.data.model.FocusSessionEntity
import kotlinx.coroutines.*

/**
 * 专注模式屏蔽服务（前台服务）
 * 
 * 该服务负责：
 * 1. 运行应用屏蔽策略（监控、提醒、拦截）
 * 2. 显示持续通知，让用户知道专注模式正在进行
 * 3. 提供服务重启后的会话恢复能力
 * 
 * 注意：这是一个前台服务，需要显示通知以提高优先级，避免被系统杀死。
 */
class FocusBlockingService : Service() {
    
    companion object {
        private const val TAG = "FocusBlockingService"
        
        // 通知渠道和 ID
        private const val NOTIFICATION_CHANNEL_ID = "focus_mode_channel"
        private const val NOTIFICATION_ID = 1001
        
        // Intent 参数
        private const val EXTRA_SESSION_ID = "session_id"
        private const val EXTRA_SCENE_ID = "scene_id"
        private const val EXTRA_SCENE_NAME = "scene_name"
        private const val EXTRA_CONFIG_JSON = "config_json"
        
        // Action
        private const val ACTION_STOP_SERVICE = "com.colin.nfc.focus.STOP_SERVICE"
        
        /**
         * 启动服务的辅助方法
         */
        fun startService(
            context: Context,
            sessionId: Long,
            sceneId: Long,
            sceneName: String,
            configJson: String? = null
        ) {
            val intent = Intent(context, FocusBlockingService::class.java).apply {
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_SCENE_ID, sceneId)
                putExtra(EXTRA_SCENE_NAME, sceneName)
                putExtra(EXTRA_CONFIG_JSON, configJson)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                @Suppress("DEPRECATION")
                context.startService(intent)
            }
            
            Log.d(TAG, "启动 FocusBlockingService")
        }
        
        /**
         * 停止服务的辅助方法
         */
        fun stopService(context: Context) {
            val intent = Intent(context, FocusBlockingService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
            
            Log.d(TAG, "停止 FocusBlockingService")
        }
        
        /**
         * 服务是否已启动（对外公开）
         */
        var isServiceStarted = false
            private set
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var appBlockerManager: AppBlockerManager? = null
    private var currentSessionId: Long = 0
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "服务创建")
        
        // 初始化 AppBlockerManager
        appBlockerManager = AppBlockerManager(this)
        
        // 注册默认策略（第一阶段仅注册 UsageStats）
        // TODO: 第二阶段添加 OverlayAlertStrategy
        // TODO: 第三阶段添加 AccessibilityStrategy
        registerDefaultStrategies()
        
        // 创建通知渠道
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "服务启动命令: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                // 用户点击通知中的"停止"按钮
                stopServiceWithReason(false)
                return START_NOT_STICKY
            }
            else -> {
                // 正常启动服务
                handleStartIntent(intent)
                
                // START_STICKY 确保服务被杀后自动重启
                // 重启后会调用 onStartCommand(null, ...)，我们需要在 onHandleIntent 中处理
                return START_STICKY
            }
        }
    }
    
    /**
     * 处理启动 Intent
     */
    private fun handleStartIntent(intent: Intent?) {
        if (FocusBlockingService.isServiceStarted) {
            Log.w(TAG, "服务已在运行，忽略重复启动")
            return
        }
        
        serviceScope.launch {
            try {
                // 1. 从 Intent 或数据库获取会话信息
                val sessionId = intent?.getLongExtra(EXTRA_SESSION_ID, 0) ?: 0
                val sceneId = intent?.getLongExtra(EXTRA_SCENE_ID, 0) ?: 0
                val sceneName = intent?.getStringExtra(EXTRA_SCENE_NAME) ?: "专注模式"
                val configJson = intent?.getStringExtra(EXTRA_CONFIG_JSON)
                
                if (sessionId > 0 && sceneId > 0) {
                    // 从 Intent 启动新会话
                    startSession(sessionId, sceneId, sceneName, configJson)
                } else {
                    // 尝试从数据库恢复活跃会话
                    restoreActiveSession()
                }
                
                // 2. 启动前台通知
                startForeground(NOTIFICATION_ID, createNotification(sceneName))
                
                FocusBlockingService.isServiceStarted = true
                
                Log.d(TAG, "服务启动成功")
            } catch (e: Exception) {
                Log.e(TAG, "启动服务失败", e)
                stopSelf()
            }
        }
    }
    
    /**
     * 启动新的专注会话
     */
    private suspend fun startSession(
        sessionId: Long,
        sceneId: Long,
        sceneName: String,
        configJson: String?
    ) {
        currentSessionId = sessionId
        
        // 解析配置（如果有）
        val config = if (!configJson.isNullOrEmpty()) {
            parseConfigFromJson(configJson)
        } else {
            // 默认配置
            BlockingConfig(
                blockedApps = emptyList(),
                focusMode = com.colin.nfc.focus.blocker.FocusMode.GENTLE
            )
        }
        
        // 更新数据库中的会话状态
        val database = NfcFocusDatabase.getDatabase(this)
        database.focusSessionDao().updateBlockingConfig(sessionId, configJson ?: "")
        
        // 启动屏蔽管理器
        appBlockerManager?.startBlocking(config)?.onFailure {
            Log.e(TAG, "启动屏蔽失败", it)
            throw it
        }
        
        Log.d(TAG, "会话 $sessionId 已启动")
    }
    
    /**
     * 从数据库恢复活跃会话
     */
    private suspend fun restoreActiveSession() {
        val database = NfcFocusDatabase.getDatabase(this)
        val activeSession = database.focusSessionDao().getActiveSession()
        
        if (activeSession != null) {
            Log.d(TAG, "恢复活跃会话: ${activeSession.id}")
            
            // 检查会话是否超时
            if (activeSession.isExpired()) {
                Log.w(TAG, "会话已超时，自动结束")
                endSession(activeSession.id, completedNormally = false)
                stopSelf()
                return
            }
            
            currentSessionId = activeSession.id
            
            // 解析配置并启动屏蔽
            val configJson = activeSession.blockingConfigJson
            if (!configJson.isNullOrEmpty()) {
                val config = parseConfigFromJson(configJson)
                appBlockerManager?.startBlocking(config)?.onFailure {
                    Log.e(TAG, "恢复屏蔽失败", it)
                }
            }
        } else {
            Log.w(TAG, "没有活跃会话，停止服务")
            stopSelf()
        }
    }
    
    /**
     * 停止服务（带原因标记）
     */
    private fun stopServiceWithReason(completedNormally: Boolean) {
        serviceScope.launch {
            try {
                // 结束当前会话
                if (currentSessionId > 0) {
                    endSession(currentSessionId, completedNormally)
                }
                
                // 停止屏蔽管理器
                appBlockerManager?.stopBlocking()
                
                // 停止前台服务
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                
                Log.d(TAG, "服务已停止，正常完成: $completedNormally")
            } catch (e: Exception) {
                Log.e(TAG, "停止服务失败", e)
            }
        }
    }
    
    /**
     * 结束会话（更新数据库）
     */
    private suspend fun endSession(sessionId: Long, completedNormally: Boolean) {
        val database = NfcFocusDatabase.getDatabase(this)
        database.focusSessionDao().endSession(
            sessionId = sessionId,
            endTime = System.currentTimeMillis(),
            completedNormally = completedNormally
        )
        
        Log.d(TAG, "会话 $sessionId 已结束")
    }
    
    /**
     * 注册默认策略
     */
    private fun registerDefaultStrategies() {
        val manager = appBlockerManager ?: return
        
        // 第一阶段：仅注册 UsageStats 监控策略
        val usageStatsStrategy = com.colin.nfc.focus.blocker.UsageStatsStrategy(this)
        manager.registerStrategy(usageStatsStrategy)
        
        Log.d(TAG, "已注册默认策略: UsageStats")
        
        // TODO: 第二阶段 - 添加覆盖窗口提醒策略
        // val overlayStrategy = OverlayAlertStrategy(this)
        // manager.registerStrategy(overlayStrategy)
        
        // TODO: 第三阶段 - 添加无障碍拦截策略
        // val accessibilityStrategy = AccessibilityStrategy(this)
        // manager.registerStrategy(accessibilityStrategy)
    }
    
    /**
     * 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "专注模式",
                NotificationManager.IMPORTANCE_LOW // 低重要性，避免打扰
            ).apply {
                description = "显示专注模式的运行状态"
                setShowBadge(false)
                enableVibration(false)
                enableLights(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 创建前台通知
     */
    private fun createNotification(sceneName: String): Notification {
        // 点击通知打开应用
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // 停止按钮
        val stopIntent = Intent(this, FocusBlockingService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("专注模式进行中")
            .setContentText("场景: $sceneName")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: 替换为自定义图标
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "结束专注",
                stopPendingIntent
            )
            .setOngoing(true) // 不可滑动删除
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    /**
     * 从 JSON 解析配置
     */
    private fun parseConfigFromJson(json: String): BlockingConfig {
        return try {
            // TODO: 使用 Gson 或其他 JSON 库解析
            // 暂时返回默认配置
            BlockingConfig(
                blockedApps = emptyList(),
                focusMode = com.colin.nfc.focus.blocker.FocusMode.GENTLE
            )
        } catch (e: Exception) {
            Log.e(TAG, "解析配置失败", e)
            BlockingConfig()
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null // 不支持绑定
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "服务销毁")
        
        serviceScope.cancel()
        
        // 清理资源
        appBlockerManager?.let {
            serviceScope.launch {
                it.stopBlocking()
            }
        }
        
        FocusBlockingService.isServiceStarted = false
    }
}
