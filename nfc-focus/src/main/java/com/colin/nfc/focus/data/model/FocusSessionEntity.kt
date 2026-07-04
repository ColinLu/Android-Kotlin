package com.colin.nfc.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 专注会话实体
 * 
 * 用于持久化存储专注会话的状态，支持服务重启后恢复。
 */
@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * 关联的场景 ID
     */
    val sceneId: Long,
    
    /**
     * 场景名称（冗余存储，避免查询时联表）
     */
    val sceneName: String,
    
    /**
     * 会话开始时间戳（毫秒）
     */
    val startTime: Long,
    
    /**
     * 会话结束时间戳（毫秒），null 表示仍在进行中
     */
    val endTime: Long? = null,
    
    /**
     * 会话是否活跃
     * true = 正在进行中
     * false = 已结束
     */
    val isActive: Boolean = true,
    
    /**
     * 触发方式（NFC/MANUAL/SCHEDULED/AUTOMATION）
     */
    val triggeredBy: String,
    
    /**
     * 是否正常完成
     * true = 用户主动结束或达到目标时间
     * false = 提前退出或被中断
     */
    val completedNormally: Boolean = true,
    
    /**
     * 屏蔽配置（JSON 序列化）
     * 包含 blockedApps、focusMode、alertType 等
     */
    val blockingConfigJson: String? = null,
    
    /**
     * 分心次数统计
     */
    val distractionCount: Int = 0,
    
    /**
     * 最后更新时间戳
     */
    val lastUpdateTime: Long = System.currentTimeMillis()
) {
    /**
     * 计算会话持续时间（秒）
     */
    fun getDurationSeconds(): Int {
        val end = endTime ?: System.currentTimeMillis()
        return ((end - startTime) / 1000).toInt()
    }
    
    /**
     * 检查会话是否已超时（超过最大时长）
     * 
     * @param maxDurationHours 最大时长（小时），默认 24 小时
     * @return 是否超时
     */
    fun isExpired(maxDurationHours: Int = 24): Boolean {
        if (!isActive) return false
        
        val now = System.currentTimeMillis()
        val durationHours = (now - startTime) / (1000 * 60 * 60)
        
        return durationHours >= maxDurationHours
    }
}
