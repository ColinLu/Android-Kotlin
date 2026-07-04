package com.colin.nfc.focus

import android.content.Context
import com.colin.nfc.focus.api.INFCFocus
import com.colin.nfc.focus.api.NFCFocusImpl

/**
 * NFC 专注模块 - 单例访问入口
 * 
 * 这是 module 对外暴露的唯一入口点，主工程通过此类访问所有功能。
 * 
 * ## 使用示例
 * 
 * ```kotlin
 * // 1. 在 Application 中初始化
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         NFCFocus.initialize(this)
 *     }
 * }
 * 
 * // 2. 在 Activity/ViewModel 中使用
 * val manager = NFCFocus.manager
 * 
 * // 创建场景
 * val scene = FocusScene(
 *     name = "工作模式",
 *     blockedApps = listOf("com.instagram.android", "com.facebook.katana"),
 *     allowListType = AllowListType.BLACKLIST,
 *     enableStartNotification = true,
 *     enableEndNotification = true
 * )
 * val result = manager.createScene(scene)
 * 
 * // 启动专注模式
 * if (result.isSuccess) {
 *     val sceneId = result.getOrNull()!!
 *     val sessionResult = manager.startFocus(sceneId, TriggerType.MANUAL)
 * }
 * 
 * // 监听状态变化
 * manager.setFocusStateListener(object : FocusStateListener {
 *     override fun onFocusStarted(session: FocusSession) {
 *         // 处理专注开始
 *     }
 *     
 *     override fun onFocusStopped(session: FocusSession, completedNormally: Boolean) {
 *         // 处理专注结束
 *     }
 * })
 * ```
 */
object NFCFocus {
    
    @Volatile
    private var INSTANCE: INFCFocus? = null
    
    /**
     * 获取管理器实例（必须在 initialize() 之后调用）
     * @throws IllegalStateException 如果未初始化
     */
    val manager: INFCFocus
        get() = INSTANCE ?: throw IllegalStateException(
            "NFCFocus not initialized. Call NFCFocus.initialize(context) first."
        )
    
    /**
     * 初始化模块（必须在应用启动时调用一次）
     * @param context Application Context
     */
    fun initialize(context: Context) {
        if (INSTANCE == null) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = NFCFocusImpl().apply {
                        initialize(context)
                    }
                }
            }
        }
    }
    
    /**
     * 检查是否已初始化
     * @return 是否已初始化
     */
    fun isInitialized(): Boolean {
        return INSTANCE != null
    }
}
