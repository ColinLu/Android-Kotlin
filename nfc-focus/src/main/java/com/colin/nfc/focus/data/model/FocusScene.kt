package com.colin.nfc.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 专注场景实体
 * 对应功能：场景配置、应用屏蔽、时间规则、例外规则
 */
@Entity(tableName = "focus_scenes")
data class FocusScene(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,             // 场景名称（如"工作"、"学习"、"睡眠"）
    val description: String?,     // 场景描述
    
    // 应用屏蔽配置
    val blockedApps: List<String>, // 被屏蔽的应用包名列表
    val allowListType: AllowListType = AllowListType.BLACKLIST, // 黑名单/白名单模式
    
    // 时间规则
    val timeRules: TimeRule?,      // 定时开启/关闭规则
    
    // 例外规则
    val exceptionRules: ExceptionRule?, // 白名单App、紧急联系人等
    
    // 引导解锁配置
    val unlockMethod: UnlockMethod = UnlockMethod.NFC_ONLY, // 解锁方式
    
    // 通知配置
    val enableStartNotification: Boolean = true,   // 专注开始通知
    val enableEndNotification: Boolean = true,     // 专注结束通知
    val enableDistractionAlert: Boolean = true,    // 分心提醒
    
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false                  // 当前是否激活
)

/**
 * 应用列表类型
 */
enum class AllowListType {
    BLACKLIST,  // 黑名单模式：屏蔽列表中的应用
    WHITELIST   // 白名单模式：只允许列表中的应用
}

/**
 * 时间规则
 */
data class TimeRule(
    val enabled: Boolean = false,
    val startTime: String? = null,    // HH:mm 格式
    val endTime: String? = null,      // HH:mm 格式
    val repeatDays: List<Int> = emptyList(), // 重复的星期几 (1-7)
    val duration: Int? = null         // 持续时长（分钟），用于倒计时专注
)

/**
 * 例外规则
 */
data class ExceptionRule(
    val allowedApps: List<String> = emptyList(),        // 白名单应用（始终可用）
    val emergencyContacts: List<String> = emptyList(),  // 紧急联系人
    val allowedTimeSlots: List<TimeSlot> = emptyList()  // 特定时间段可用
)

data class TimeSlot(
    val startTime: String,  // HH:mm
    val endTime: String     // HH:mm
)

/**
 * 解锁方式
 */
enum class UnlockMethod {
    NFC_ONLY,           // 仅NFC卡
    PASSWORD,           // 密码
    FACE_ID,            // Face ID / 指纹
    NFC_OR_PASSWORD,    // NFC或密码
    NFC_AND_PASSWORD    // NFC和密码（双重验证）
}
