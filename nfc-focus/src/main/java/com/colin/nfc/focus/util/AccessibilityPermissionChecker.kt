package com.colin.nfc.focus.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.util.Log
import com.colin.nfc.focus.service.FocusAccessibilityService

/**
 * 无障碍权限检查工具
 * 
 * 用于检测和引导用户开启无障碍服务
 */
object AccessibilityPermissionChecker {
    
    private const val TAG = "AccessibilityPermCheck"
    
    /**
     * 检查无障碍服务是否已启用
     * 
     * @param context 上下文
     * @return 是否已启用
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        return try {
            val accessibilityManager = context.getSystemService(
                Context.ACCESSIBILITY_SERVICE
            ) as android.view.accessibility.AccessibilityManager
            
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_GENERIC
            )
            
            val serviceName = FocusAccessibilityService::class.java.name
            val isEnabled = enabledServices.any { service ->
                service.resolveInfo.serviceInfo.packageName == context.packageName &&
                service.resolveInfo.serviceInfo.name == serviceName.substringAfterLast('.')
            }
            
            Log.d(TAG, "无障碍服务状态: $isEnabled")
            isEnabled
        } catch (e: Exception) {
            Log.e(TAG, "检查无障碍服务状态失败", e)
            false
        }
    }
    
    /**
     * 获取需要重新开启服务的提示消息
     * 
     * 当检测到服务被用户强制关闭时调用
     * 
     * @return 提示消息
     */
    fun getServiceReopenMessage(): String {
        return buildString {
            appendLine("⚠️ 检测到无障碍服务已关闭")
            appendLine()
            appendLine("专注模式的强制拦截功能需要使用无障碍服务。")
            appendLine()
            appendLine("可能原因：")
            appendLine("- 用户在设置中手动关闭")
            appendLine("- 系统重启后服务未自动启动")
            appendLine("- 系统资源紧张被清理")
            appendLine()
            appendLine("请重新开启以继续使用严格模式。")
        }
    }
}
