package com.colin.nfc.focus.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.colin.nfc.focus.data.local.NfcFocusDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 专注模式无障碍服务
 * 
 * 功能：
 * 1. 监听应用切换事件
 * 2. 检测是否打开了分心应用
 * 3. 立即拦截并显示提醒
 * 
 * 重要：此服务是实现"严格模式"强制拦截的核心
 * 
 * 配置说明：
 * - 需要在系统设置中手动开启无障碍权限
 * - 如果用户关闭服务，会检测到并引导重新开启
 */
class FocusAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "FocusAccessibilitySvc"
        
        /**
         * 检查服务是否正在运行
         */
        var isServiceRunning = false
            private set
        
        /**
         * 分心应用列表（由策略管理器更新）
         */
        var blockedApps: Set<String> = emptySet()
        
        /**
         * 拦截回调
         */
        var onAppBlocked: ((packageName: String) -> Unit)? = null
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        isServiceRunning = true
        Log.d(TAG, "无障碍服务已连接")
        
        // 配置服务
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
            notificationTimeout = 100
        }
        serviceInfo = info
    }
    
    override fun onUnbind(intent: Intent?): Boolean {
        isServiceRunning = false
        Log.w(TAG, "无障碍服务已断开")
        return super.onUnbind(intent)
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.packageName == null) {
            return
        }
        
        val packageName = event.packageName.toString()
        
        // 检查是否为分心应用
        if (blockedApps.contains(packageName)) {
            Log.w(TAG, "检测到分心应用: $packageName")
            
            // 触发拦截回调
            onAppBlocked?.invoke(packageName)
            
            // 可选：立即返回桌面（需要额外权限）
            // performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "无障碍服务被中断")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        Log.e(TAG, "无障碍服务已销毁")
    }
}
