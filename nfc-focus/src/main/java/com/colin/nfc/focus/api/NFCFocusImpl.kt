package com.colin.nfc.focus.api

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.colin.nfc.focus.blocker.BlockingConfig
import com.colin.nfc.focus.blocker.FocusMode
import com.colin.nfc.focus.data.local.NfcFocusDatabase
import com.colin.nfc.focus.data.model.FocusScene
import com.colin.nfc.focus.data.model.FocusSessionEntity
import com.colin.nfc.focus.data.model.UsageStat
import com.colin.nfc.focus.nfc.NfcCompatibilityChecker
import com.colin.nfc.focus.nfc.NfcEnableGuide
import com.colin.nfc.focus.nfc.NfcManager
import com.colin.nfc.focus.nfc.NfcSupportResult
import com.colin.nfc.focus.nfc.NfcTagHelper
import com.colin.nfc.focus.service.FocusBlockingService
import com.colin.nfc.focus.util.i
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * INFCFocus 默认实现
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class NFCFocusImpl : INFCFocus {

    private var context: Context? = null
    private var database: NfcFocusDatabase? = null
    
    // NFC 管理器
    private var nfcManager: NfcManager? = null
    private var nfcTagHelper: NfcTagHelper? = null
    private var nfcCompatibilityChecker: NfcCompatibilityChecker? = null
    
    // 当前活跃的专注会话
    @Volatile
    private var currentSession: FocusSession? = null
    
    // 监听器
    private var focusStateListener: FocusStateListener? = null
    private var nfcScanListener: NfcScanListener? = null
    
    // 线程安全锁
    private val sessionMutex = Mutex()
    

    override fun initialize(context: Context) {
        this.context = context.applicationContext
        database = NfcFocusDatabase.getDatabase(context)
        
        // 初始化 NFC 管理器
        nfcManager = NfcManager(context)
        nfcTagHelper = NfcTagHelper(database!!)
        nfcCompatibilityChecker = NfcCompatibilityChecker(context)
    }
    
    // ==================== NFC 功能 ====================
    
    override fun checkNfcSupport(): NfcSupportResult {
        val checker = nfcCompatibilityChecker ?: return NfcSupportResult.NoHardware
        return checker.checkNfcSupport()
    }
    
    override fun getNfcEnableGuide(): NfcEnableGuide {
        val checker = nfcCompatibilityChecker ?: return NfcEnableGuide(
            title = "开启 NFC",
            description = "请在系统设置中开启 NFC 功能",
            steps = listOf(
                "1. 打开「设置」",
                "2. 找到「连接」或「无线和网络」",
                "3. 开启「NFC」开关"
            )
        )
        return checker.getNfcEnableGuide()
    }
    
    override fun scanTag(activity: Activity): Flow<NfcScanResult> {
        val manager = nfcManager ?: return kotlinx.coroutines.flow.flowOf(
            NfcScanResult.Error("NFC 未初始化")
        )
        
        if (!manager.isNfcAvailable()) {
            return kotlinx.coroutines.flow.flowOf(
                NfcScanResult.Error("设备不支持 NFC 或 NFC 未启用")
            )
        }
        
        return manager.scanTag(activity)
    }
    
    override suspend fun writeSceneToTag(tagUid: String, sceneId: Long): Result<Unit> {
        return try {
            val tagHelper = nfcTagHelper ?: return Result.failure(
                IllegalStateException("NFC 未初始化")
            )
            
            // 将标签与场景绑定
            val success = tagHelper.bindTagToScene(tagUid, sceneId)
            
            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("绑定标签失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== 专注模式控制 ====================
    
    override suspend fun startFocus(
        sceneId: Long,
        triggeredBy: TriggerType
    ): Result<FocusSession> {
        return try {
            val db = database ?: return Result.failure(IllegalStateException("Module not initialized"))
            val context = context ?: return Result.failure(IllegalStateException("Module not initialized"))
            
            // 1. 获取场景配置
            val scene = db.focusSceneDao().getSceneById(sceneId)
                ?: return Result.failure(IllegalArgumentException("Scene not found: $sceneId"))
            
            // 2. 创建屏蔽配置（第一阶段使用默认配置）
            val blockingConfig = BlockingConfig(
                blockedApps = emptyList(), // TODO: 从场景中读取
                focusMode = FocusMode.GENTLE,
                checkInterval = 1000L
            )
            
            // 3. 序列化配置为 JSON
            val gson = Gson()
            val configJson = gson.toJson(blockingConfig)
            
            // 4. 创建会话实体并保存到数据库
            val sessionEntity = FocusSessionEntity(
                sceneId = sceneId,
                sceneName = scene.name,
                startTime = System.currentTimeMillis(),
                endTime = null,
                isActive = true,
                triggeredBy = triggeredBy.name,
                completedNormally = true,
                blockingConfigJson = configJson,
                distractionCount = 0
            )
            val sessionId = db.focusSessionDao().insertSession(sessionEntity)
            
            // 5. 创建 API 层的会话对象
            val session = FocusSession(
                sessionId = sessionId,
                sceneId = sceneId,
                sceneName = scene.name,
                startTime = sessionEntity.startTime,
                endTime = null,
                triggeredBy = triggeredBy,
                isActive = true
            )
            
            // 6. 更新当前会话
            sessionMutex.withLock {
                currentSession = session
            }
            
            // 7. 启动前台服务
            FocusBlockingService.startService(
                context = context,
                sessionId = sessionId,
                sceneId = sceneId,
                sceneName = scene.name,
                configJson = configJson
            )
            
            // 8. 通知监听器
            focusStateListener?.onFocusStarted(session)
            
            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun stopFocus(
        sessionId: Long,
        completedNormally: Boolean
    ): Result<Unit> {
        return try {
            val db = database ?: return Result.failure(IllegalStateException("Module not initialized"))
            val context = context ?: return Result.failure(IllegalStateException("Module not initialized"))
            
            // 1. 获取会话记录
            val sessionEntity = db.focusSessionDao().getSessionById(sessionId)
                ?: return Result.failure(IllegalArgumentException("Session not found: $sessionId"))
            
            // 2. 结束会话（更新数据库）
            db.focusSessionDao().endSession(
                sessionId = sessionId,
                endTime = System.currentTimeMillis(),
                completedNormally = completedNormally
            )
            
            // 3. 清除当前会话
            sessionMutex.withLock {
                currentSession = currentSession?.copy(
                    endTime = sessionEntity.endTime,
                    isActive = false
                )
            }
            
            // 4. 停止前台服务
            FocusBlockingService.stopService(context)
            
            // 5. 通知监听器
            val session = currentSession ?: return Result.failure(IllegalStateException("No active session"))
            focusStateListener?.onFocusStopped(session, completedNormally)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getCurrentSession(): FocusSession? {
        return currentSession
    }
    
    // ==================== 场景管理 ====================
    
    override suspend fun createScene(scene: FocusScene): Result<Long> {
        return try {
            val db = database ?: return Result.failure(IllegalStateException("Module not initialized"))
            val id = db.focusSceneDao().insertFocusScene(scene)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateScene(scene: FocusScene): Result<Unit> {
        return try {
            val db = database ?: return Result.failure(IllegalStateException("Module not initialized"))
            db.focusSceneDao().updateFocusScene(scene)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteScene(sceneId: Long): Result<Unit> {
        return try {
            val db = database ?: return Result.failure(IllegalStateException("Module not initialized"))
            db.focusSceneDao().deleteFocusSceneById(sceneId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllScenes(): List<FocusScene> {
        val db = database ?: return emptyList()
        return db.focusSceneDao().getAllFocusScenes()
    }
    
    override suspend fun getScene(sceneId: Long): FocusScene? {
        val db = database ?: return null
        return db.focusSceneDao().getSceneById(sceneId)
    }
    
    override suspend fun getActiveScenes(): List<FocusScene> {
        val db = database ?: return emptyList()
        return db.focusSceneDao().getActiveFocusScenes()
    }
    
    // ==================== 使用统计 ====================
    
    override suspend fun getUsageStats(startDate: Long, endDate: Long): List<UsageStat> {
        val db = database ?: return emptyList()
        return db.usageStatDao().getUsageStatsByDateRange(startDate, endDate)
    }
    
    override suspend fun getTodayStats(): List<UsageStat> {
        val db = database ?: return emptyList()
        val todayStart = getTodayStartTimestamp()
        val todayEnd = System.currentTimeMillis()
        return db.usageStatDao().getUsageStatsByDateRange(todayStart, todayEnd)
    }
    
    override suspend fun getWeekStats(): List<UsageStat> {
        val db = database ?: return emptyList()
        val weekStart = getWeekStartTimestamp()
        val weekEnd = System.currentTimeMillis()
        return db.usageStatDao().getUsageStatsByDateRange(weekStart, weekEnd)
    }
    
    override suspend fun clearUsageStats(): Result<Unit> {
        return try {
            val db = database ?: return Result.failure(IllegalStateException("Module not initialized"))
            db.usageStatDao().deleteAllUsageStats()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== 权限管理 ====================
    
    override suspend fun requestUsageStatsPermission(activity: Activity): Result<Unit> {
        return try {
            val context = context ?: return Result.failure(IllegalStateException("Module not initialized"))
            
            // 获取 UsageStats 策略并请求权限
            val strategy = com.colin.nfc.focus.blocker.UsageStatsStrategy(context)
            strategy.requestPermission(activity)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun hasUsageStatsPermission(): Boolean {
        val context = context ?: return false
        
        // 检查 UsageStats 权限
        val strategy = com.colin.nfc.focus.blocker.UsageStatsStrategy(context)
        return strategy.isPermissionGranted(context)
    }
    
    override suspend fun requestPermissions(activity: Activity): Result<Unit> {
        // TODO: 实现其他权限请求逻辑
        return Result.success(Unit)
    }
    
    override fun hasRequiredPermissions(): Boolean {
        // 第一阶段仅检查 UsageStats 权限
        return hasUsageStatsPermission()
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun requestOverlayPermission(activity: Activity): Result<Unit> {
        return try {
            val context = context ?: return Result.failure(IllegalStateException("Module not initialized"))
            
            // 检查当前权限状态
            if (hasOverlayPermission()) {
                return Result.success(Unit)
            }
            
            // 打开悬浮窗权限设置页面
            val intent = android.content.Intent(
                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${context.packageName}".toUri()
            )
            activity.startActivity(intent)
            android.util.Log.d("NFCFocusManager", "已打开悬浮窗权限设置页面")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NFCFocusManager", "请求悬浮窗权限失败", e)
            Result.failure(e)
        }
    }
    
    override fun hasOverlayPermission(): Boolean {
        val context = context ?: return false
        
        // Android 6.0+ 需要检查悬浮窗权限
        return android.provider.Settings.canDrawOverlays(context)
    }
    
    override suspend fun requestAccessibilityPermission(activity: Activity): Result<Unit> {
        return try {
            val context = context ?: return Result.failure(IllegalStateException("Module not initialized"))
            
            // 创建无障碍策略实例
            val strategy = com.colin.nfc.focus.blocker.AccessibilityStrategy()
            
            // 请求权限（会打开无障碍设置页面）
            strategy.requestPermission(activity)
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("NFCFocusManager", "请求无障碍权限失败", e)
            Result.failure(e)
        }
    }
    
    override fun isAccessibilityServiceEnabled(): Boolean {
        val context = context ?: return false
        return com.colin.nfc.focus.util.AccessibilityPermissionChecker.isAccessibilityServiceEnabled(context)
    }
    
    override fun getAccessibilityAuthGuide(): com.colin.nfc.focus.blocker.AccessibilityAuthGuide? {
        val strategy = com.colin.nfc.focus.blocker.AccessibilityStrategy()
        return strategy.getAuthorizationGuide()
    }
    
    // ==================== UI 自定义 ====================
    
    private var alertViewProvider: com.colin.nfc.focus.ui.provider.IAlertViewProvider? = null
    private var notificationProvider: com.colin.nfc.focus.ui.provider.INotificationProvider? = null
    
    override fun setAlertViewProvider(provider: com.colin.nfc.focus.ui.provider.IAlertViewProvider) {
        this.alertViewProvider = provider
        android.util.Log.d("NFCFocusManager", "已设置自定义覆盖窗口提供者")
    }
    
    override fun setNotificationProvider(provider: com.colin.nfc.focus.ui.provider.INotificationProvider) {
        this.notificationProvider = provider
        // 创建通知渠道
        val context = context
        if (context != null) {
            provider.createNotificationChannels(context)
            "已设置自定义通知提供者".i("NFCFocusManager")
        }
    }
    
    // ==================== 监听器 ====================
    
    override fun setFocusStateListener(listener: FocusStateListener?) {
        focusStateListener = listener
    }
    
    override fun setNfcScanListener(listener: NfcScanListener?) {
        nfcScanListener = listener
    }
    
    // ==================== 辅助方法 ====================
    
    private fun getTodayStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getWeekStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun TriggerType.toTriggerType(): com.colin.nfc.focus.data.model.TriggerType {
        return when (this) {
            TriggerType.NFC -> com.colin.nfc.focus.data.model.TriggerType.NFC
            TriggerType.MANUAL -> com.colin.nfc.focus.data.model.TriggerType.MANUAL
            TriggerType.SCHEDULED -> com.colin.nfc.focus.data.model.TriggerType.SCHEDULED
            TriggerType.AUTOMATION -> com.colin.nfc.focus.data.model.TriggerType.AUTOMATION
        }
    }
}
