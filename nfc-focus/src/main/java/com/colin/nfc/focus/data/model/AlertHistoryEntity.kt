package com.colin.nfc.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 提醒历史实体
 * 
 * 记录每次分心提醒的详细信息，用于统计和分析用户行为模式
 * 
 * @property id 主键 ID
 * @property sessionId 关联的专注会话 ID
 * @property packageName 分心应用的包名
 * @property appName 分心应用的名称
 * @property alertTime 提醒时间戳（毫秒）
 * @property alertLevel 提醒级别（1=温和, 2=标准, 3=强制）
 * @property actionTaken 用户采取的行动（RETURN_TO_FOCUS/DISMISS/TEMPORARY_EXIT/IGNORED）
 * @property focusMode 当前专注模式
 * @property strategyType 触发提醒的策略类型
 */
@Entity(tableName = "alert_history")
data class AlertHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val sessionId: Long,
    
    val packageName: String,
    
    val appName: String,
    
    val alertTime: Long = System.currentTimeMillis(),
    
    val alertLevel: Int = 1,
    
    val actionTaken: String = ActionTaken.IGNORED.name,
    
    val focusMode: String = FocusMode.GENTLE.name,
    
    val strategyType: String = BlockingStrategyType.USAGE_STATS.name,
    
    val dismissedCountdownSeconds: Int? = null
) {
    /**
     * 用户行动枚举
     */
    enum class ActionTaken {
        RETURN_TO_FOCUS,    // 返回专注
        DISMISS,            // 关闭提醒
        TEMPORARY_EXIT,     // 临时退出
        IGNORED             // 忽略（超时自动关闭）
    }
    
    /**
     * 专注模式枚举（冗余定义，避免循环依赖）
     */
    enum class FocusMode {
        GENTLE,
        BALANCED,
        STRICT
    }
    
    /**
     * 策略类型枚举（冗余定义，避免循环依赖）
     */
    enum class BlockingStrategyType {
        USAGE_STATS,
        OVERLAY_ALERT,
        ACCESSIBILITY,
        DEVICE_POLICY
    }
}
