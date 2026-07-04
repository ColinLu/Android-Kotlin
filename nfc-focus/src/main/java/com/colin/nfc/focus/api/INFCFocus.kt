package com.colin.nfc.focus.api

import android.app.Activity
import android.content.Context
import com.colin.nfc.focus.data.model.FocusScene
import com.colin.nfc.focus.data.model.UsageStat
import com.colin.nfc.focus.nfc.NfcEnableGuide
import com.colin.nfc.focus.nfc.NfcSupportResult
import kotlinx.coroutines.flow.Flow

/**
 * NFC 专注模式管理器 - 主入口 API
 * 
 * 这是 module 对外暴露的核心接口，提供所有功能的统一访问入口。
 * 主工程通过此接口与 NFC 专注模块交互。
 */
interface INFCFocus {

    /**
     * 初始化模块（必须在应用启动时调用）
     * @param context Application Context
     */
    fun initialize(context: Context)

    // ==================== NFC 功能 ====================

    /**
     * 检查 NFC 支持状态
     * @return NfcSupportResult NFC 支持结果
     */
    fun checkNfcSupport(): NfcSupportResult

    /**
     * 获取 NFC 开启引导信息
     * @return NfcEnableGuide 引导信息
     */
    fun getNfcEnableGuide(): NfcEnableGuide

    /**
     * 扫描 NFC 标签
     * @param activity 当前 Activity（用于前台 NFC 读取）
     * @return Flow<NfcScanResult> 扫描结果流
     */
    fun scanTag(activity: Activity): Flow<NfcScanResult>

    /**
     * 将场景配置写入 NFC 标签
     * @param tagUid NFC 标签 UID
     * @param sceneId 场景 ID
     * @return Result<Unit> 写入结果
     */
    suspend fun writeSceneToTag(tagUid: String, sceneId: Long): Result<Unit>

    // ==================== 专注模式控制 ====================

    /**
     * 启动专注模式
     * @param sceneId 场景 ID
     * @param triggeredBy 触发方式（NFC/MANUAL/SCHEDULED）
     * @return Result<FocusSession> 专注会话信息
     */
    suspend fun startFocus(
        sceneId: Long, triggeredBy: TriggerType = TriggerType.MANUAL
    ): Result<FocusSession>

    /**
     * 停止专注模式
     * @param sessionId 专注会话 ID
     * @param completedNormally 是否正常完成（false 表示提前退出）
     * @return Result<Unit> 停止结果
     */
    suspend fun stopFocus(
        sessionId: Long, completedNormally: Boolean = true
    ): Result<Unit>

    /**
     * 获取当前活跃的专注会话
     * @return 当前会话或 null
     */
    fun getCurrentSession(): FocusSession?

    // ==================== 场景管理 ====================

    /**
     * 创建新的专注场景
     * @param scene 场景配置
     * @return Result<Long> 创建的场景 ID
     */
    suspend fun createScene(scene: FocusScene): Result<Long>

    /**
     * 更新现有场景
     * @param scene 更新后的场景配置
     * @return Result<Unit> 更新结果
     */
    suspend fun updateScene(scene: FocusScene): Result<Unit>

    /**
     * 删除场景
     * @param sceneId 场景 ID
     * @return Result<Unit> 删除结果
     */
    suspend fun deleteScene(sceneId: Long): Result<Unit>

    /**
     * 获取所有场景列表
     * @return 场景列表
     */
    suspend fun getAllScenes(): List<FocusScene>

    /**
     * 获取指定场景
     * @param sceneId 场景 ID
     * @return 场景配置或 null
     */
    suspend fun getScene(sceneId: Long): FocusScene?

    /**
     * 获取当前活跃的场景列表
     * @return 活跃场景列表
     */
    suspend fun getActiveScenes(): List<FocusScene>

    // ==================== 使用统计 ====================

    /**
     * 获取使用统计数据
     * @param startDate 开始时间戳（毫秒）
     * @param endDate 结束时间戳（毫秒）
     * @return 统计数据列表
     */
    suspend fun getUsageStats(
        startDate: Long, endDate: Long
    ): List<UsageStat>

    /**
     * 获取今日统计数据
     * @return 今日统计数据
     */
    suspend fun getTodayStats(): List<UsageStat>

    /**
     * 获取本周统计数据
     * @return 本周统计数据
     */
    suspend fun getWeekStats(): List<UsageStat>

    /**
     * 清空统计数据
     * @return Result<Unit> 清空结果
     */
    suspend fun clearUsageStats(): Result<Unit>

    // ==================== 权限管理 ====================

    /**
     * 请求使用情况统计权限（用于应用监控）
     * @param activity 当前 Activity
     * @return Result<Unit> 授权结果
     */
    suspend fun requestUsageStatsPermission(activity: Activity): Result<Unit>

    /**
     * 检查是否有使用情况统计权限
     * @return 是否已授权
     */
    fun hasUsageStatsPermission(): Boolean

    /**
     * 请求必要权限（Screen Time / FamilyControls）
     * @param activity 当前 Activity
     * @return Result<Unit> 授权结果
     */
    suspend fun requestPermissions(activity: Activity): Result<Unit>

    /**
     * 检查是否已授予必要权限
     * @return 是否已授权
     */
    fun hasRequiredPermissions(): Boolean

    /**
     * 请求悬浮窗权限（用于覆盖窗口提醒）
     * @param activity 当前 Activity
     * @return Result<Unit> 授权结果
     */
    suspend fun requestOverlayPermission(activity: Activity): Result<Unit>

    /**
     * 检查是否有悬浮窗权限
     * @return 是否已授权
     */
    fun hasOverlayPermission(): Boolean

    /**
     * 请求无障碍服务权限（用于强制拦截）
     * @param activity 当前 Activity
     * @return Result<Unit> 授权结果
     */
    suspend fun requestAccessibilityPermission(activity: Activity): Result<Unit>

    /**
     * 检查无障碍服务是否已启用
     * @return 是否已启用
     */
    fun isAccessibilityServiceEnabled(): Boolean

    /**
     * 获取无障碍服务授权引导信息
     * @return 引导信息
     */
    fun getAccessibilityAuthGuide(): com.colin.nfc.focus.blocker.AccessibilityAuthGuide?

    // ==================== UI 自定义 ====================

    /**
     * 设置自定义覆盖窗口提供者
     * @param provider UI 提供者实现
     */
    fun setAlertViewProvider(provider: com.colin.nfc.focus.ui.provider.IAlertViewProvider)

    /**
     * 设置自定义通知提供者
     * @param provider 通知提供者实现
     */
    fun setNotificationProvider(provider: com.colin.nfc.focus.ui.provider.INotificationProvider)

    // ==================== 监听器 ====================

    /**
     * 设置专注状态变化监听器
     * @param listener 状态监听器
     */
    fun setFocusStateListener(listener: FocusStateListener?)

    /**
     * 设置 NFC 扫描结果监听器
     * @param listener NFC 监听器
     */
    fun setNfcScanListener(listener: NfcScanListener?)
}

/**
 * 专注会话信息
 */
data class FocusSession(
    val sessionId: Long,
    val sceneId: Long,
    val sceneName: String,
    val startTime: Long,
    val endTime: Long?,
    val triggeredBy: TriggerType,
    val isActive: Boolean
)

/**
 * 触发方式枚举
 */
enum class TriggerType {
    NFC,           // NFC 标签触发
    MANUAL,        // 手动触发
    SCHEDULED,     // 定时触发
    AUTOMATION     // 自动化触发（Siri/快捷指令）
}

/**
 * NFC 扫描结果
 */
sealed class NfcScanResult {
    data class Success(val tagUid: String, val tagName: String?) : NfcScanResult()
    data class Error(val message: String) : NfcScanResult()
    object Cancelled : NfcScanResult()
}

/**
 * 专注状态监听器
 */
interface FocusStateListener {
    /**
     * 专注模式启动
     * @param session 专注会话
     */
    fun onFocusStarted(session: FocusSession)

    /**
     * 专注模式停止
     * @param session 专注会话
     * @param completedNormally 是否正常完成
     */
    fun onFocusStopped(session: FocusSession, completedNormally: Boolean)

    /**
     * 检测到分心行为
     * @param packageName 分心应用包名
     */
    fun onDistractionDetected(packageName: String)
}

/**
 * NFC 扫描监听器
 */
interface NfcScanListener {
    /**
     * 成功扫描到 NFC 标签
     * @param tagUid 标签 UID
     * @param tagName 标签名称（如果已绑定场景）
     */
    fun onTagScanned(tagUid: String, tagName: String?)

    /**
     * NFC 扫描错误
     * @param error 错误信息
     */
    fun onScanError(error: String)
}
