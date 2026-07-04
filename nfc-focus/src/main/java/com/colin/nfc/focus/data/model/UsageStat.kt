package com.colin.nfc.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 使用统计实体
 * 对应功能：使用统计（屏蔽次数、专注时长、分心App统计）
 */
@Entity(tableName = "usage_stats")
data class UsageStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sceneId: Long,                              // 关联的场景ID
    val sceneName: String,                          // 场景名称
    val startTime: Long,                            // 专注开始时间
    val endTime: Long?,                             // 专注结束时间
    val duration: Int,                              // 专注时长（秒）
    val distractionCount: Int = 0,                  // 分心次数
    val distractedApps: List<String> = emptyList(), // 尝试打开的被屏蔽应用
    val triggeredBy: TriggerType = TriggerType.NFC, // 触发方式
    val completedNormally: Boolean = true           // 是否正常完成（非强制解锁）
)

enum class TriggerType {
    NFC,            // NFC卡触发
    MANUAL,         // 手动触发
    SCHEDULED,      // 定时触发
    AUTOMATION      // 自动化触发（Siri/快捷指令）
}
