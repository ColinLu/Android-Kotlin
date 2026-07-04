package com.colin.nfc.focus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.colin.nfc.focus.data.model.AlertHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * 提醒历史数据访问对象
 * 
 * 提供提醒历史的 CRUD 操作和统计分析功能
 */
@Dao
interface AlertHistoryDao {
    
    /**
     * 插入提醒记录
     * @param entity 提醒历史实体
     * @return 插入的记录 ID
     */
    @Insert
    suspend fun insertAlert(entity: AlertHistoryEntity): Long
    
    /**
     * 批量插入提醒记录
     * @param entities 提醒历史实体列表
     */
    @Insert
    suspend fun insertAlerts(entities: List<AlertHistoryEntity>)
    
    /**
     * 查询指定会话的所有提醒记录
     * @param sessionId 会话 ID
     * @return 提醒历史列表
     */
    @Query("SELECT * FROM alert_history WHERE sessionId = :sessionId ORDER BY alertTime DESC")
    suspend fun getAlertsBySession(sessionId: Long): List<AlertHistoryEntity>
    
    /**
     * 查询今日提醒记录
     * @param startOfDay 今日开始时间戳
     * @return 今日提醒列表
     */
    @Query("SELECT * FROM alert_history WHERE alertTime >= :startOfDay ORDER BY alertTime DESC")
    suspend fun getTodayAlerts(startOfDay: Long): List<AlertHistoryEntity>
    
    /**
     * 查询指定应用的提醒历史
     * @param packageName 应用包名
     * @param limit 最大返回数量
     * @return 提醒历史列表
     */
    @Query("SELECT * FROM alert_history WHERE packageName = :packageName ORDER BY alertTime DESC LIMIT :limit")
    suspend fun getAlertsByPackage(packageName: String, limit: Int = 50): List<AlertHistoryEntity>
    
    /**
     * 统计今日提醒次数
     * @param startOfDay 今日开始时间戳
     * @return 提醒次数
     */
    @Query("SELECT COUNT(*) FROM alert_history WHERE alertTime >= :startOfDay")
    suspend fun countTodayAlerts(startOfDay: Long): Int
    
    /**
     * 统计指定应用的提醒次数
     * @param packageName 应用包名
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 提醒次数
     */
    @Query("SELECT COUNT(*) FROM alert_history WHERE packageName = :packageName AND alertTime BETWEEN :startTime AND :endTime")
    suspend fun countAlertsByPackage(
        packageName: String,
        startTime: Long,
        endTime: Long
    ): Int
    
    /**
     * 统计各提醒级别的数量
     * @param startOfDay 今日开始时间戳
     * @return Map<alertLevel, count>
     */
    @Query("""
        SELECT alertLevel, COUNT(*) as count 
        FROM alert_history 
        WHERE alertTime >= :startOfDay 
        GROUP BY alertLevel
    """)
    suspend fun getAlertLevelStats(startOfDay: Long): List<AlertLevelStat>
    
    /**
     * 统计用户行动分布
     * @param startOfDay 今日开始时间戳
     * @return Map<actionTaken, count>
     */
    @Query("""
        SELECT actionTaken, COUNT(*) as count 
        FROM alert_history 
        WHERE alertTime >= :startOfDay 
        GROUP BY actionTaken
    """)
    suspend fun getActionStats(startOfDay: Long): List<ActionStat>
    
    /**
     * 获取最常分心的应用排行
     * @param startOfDay 今日开始时间戳
     * @param limit 返回数量
     * @return 应用包名和提醒次数列表
     */
    @Query("""
        SELECT packageName, appName, COUNT(*) as count 
        FROM alert_history 
        WHERE alertTime >= :startOfDay 
        GROUP BY packageName 
        ORDER BY count DESC 
        LIMIT :limit
    """)
    suspend fun getTopDistractedApps(startOfDay: Long, limit: Int = 10): List<TopDistractedApp>
    
    /**
     * 删除指定会话的提醒记录
     * @param sessionId 会话 ID
     */
    @Query("DELETE FROM alert_history WHERE sessionId = :sessionId")
    suspend fun deleteAlertsBySession(sessionId: Long)
    
    /**
     * 删除指定日期之前的所有提醒记录
     * @param beforeTime 时间戳（删除此时间之前的记录）
     */
    @Query("DELETE FROM alert_history WHERE alertTime < :beforeTime")
    suspend fun deleteAlertsBeforeTime(beforeTime: Long)
    
    /**
     * 清空所有提醒记录
     */
    @Query("DELETE FROM alert_history")
    suspend fun deleteAllAlerts()
    
    /**
     * 观察今日提醒数量变化
     * @param startOfDay 今日开始时间戳
     * @return 提醒数量流
     */
    @Query("SELECT COUNT(*) FROM alert_history WHERE alertTime >= :startOfDay")
    fun observeTodayAlertCount(startOfDay: Long): Flow<Int>
}

/**
 * 提醒级别统计数据
 */
data class AlertLevelStat(
    val alertLevel: Int,
    val count: Int
)

/**
 * 用户行动统计数据
 */
data class ActionStat(
    val actionTaken: String,
    val count: Int
)

/**
 * 最常分心的应用
 */
data class TopDistractedApp(
    val packageName: String,
    val appName: String,
    val count: Int
)
