package com.colin.nfc.focus.data.local

import androidx.room.*
import com.colin.nfc.focus.data.model.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 专注会话数据访问对象
 */
@Dao
interface FocusSessionDao {
    
    /**
     * 插入新会话
     * 
     * @param session 会话实体
     * @return 生成的会话 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long
    
    /**
     * 更新会话
     * 
     * @param session 更新后的会话实体
     */
    @Update
    suspend fun updateSession(session: FocusSessionEntity)
    
    /**
     * 删除会话
     * 
     * @param session 要删除的会话
     */
    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)
    
    /**
     * 根据 ID 获取会话
     * 
     * @param sessionId 会话 ID
     * @return 会话实体或 null
     */
    @Query("SELECT * FROM focus_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): FocusSessionEntity?
    
    /**
     * 获取当前活跃的会话
     * 
     * @return 活跃会话或 null
     */
    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveSession(): FocusSessionEntity?
    
    /**
     * 获取所有活跃会话（理论上应该只有一个）
     * 
     * @return 活跃会话列表
     */
    @Query("SELECT * FROM focus_sessions WHERE isActive = 1 ORDER BY startTime DESC")
    suspend fun getAllActiveSessions(): List<FocusSessionEntity>
    
    /**
     * 获取最近的 N 个会话
     * 
     * @param limit 数量限制
     * @return 会话列表
     */
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC LIMIT :limit")
    suspend fun getRecentSessions(limit: Int = 10): List<FocusSessionEntity>
    
    /**
     * 获取指定日期范围内的会话
     * 
     * @param startDate 开始时间戳
     * @param endDate 结束时间戳
     * @return 会话列表
     */
    @Query("SELECT * FROM focus_sessions WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    suspend fun getSessionsByDateRange(startDate: Long, endDate: Long): List<FocusSessionEntity>
    
    /**
     * 获取指定场景的会话历史
     * 
     * @param sceneId 场景 ID
     * @param limit 数量限制
     * @return 会话列表
     */
    @Query("SELECT * FROM focus_sessions WHERE sceneId = :sceneId ORDER BY startTime DESC LIMIT :limit")
    suspend fun getSessionsBySceneId(sceneId: Long, limit: Int = 20): List<FocusSessionEntity>
    
    /**
     * 更新会话结束时间
     * 
     * @param sessionId 会话 ID
     * @param endTime 结束时间戳
     * @param completedNormally 是否正常完成
     */
    @Query("UPDATE focus_sessions SET endTime = :endTime, isActive = 0, completedNormally = :completedNormally, lastUpdateTime = :lastUpdateTime WHERE id = :sessionId")
    suspend fun endSession(sessionId: Long, endTime: Long, completedNormally: Boolean, lastUpdateTime: Long = System.currentTimeMillis())
    
    /**
     * 增加分心次数
     * 
     * @param sessionId 会话 ID
     */
    @Query("UPDATE focus_sessions SET distractionCount = distractionCount + 1, lastUpdateTime = :lastUpdateTime WHERE id = :sessionId")
    suspend fun incrementDistractionCount(sessionId: Long, lastUpdateTime: Long = System.currentTimeMillis())
    
    /**
     * 更新屏蔽配置
     * 
     * @param sessionId 会话 ID
     * @param configJson JSON 序列化的配置
     */
    @Query("UPDATE focus_sessions SET blockingConfigJson = :configJson, lastUpdateTime = :lastUpdateTime WHERE id = :sessionId")
    suspend fun updateBlockingConfig(sessionId: Long, configJson: String, lastUpdateTime: Long = System.currentTimeMillis())
    
    /**
     * 标记超时会话为已结束
     * 
     * @param maxDurationHours 最大时长（小时）
     * @return 更新的记录数
     */
    @Query("UPDATE focus_sessions SET isActive = 0, endTime = :now, lastUpdateTime = :now WHERE isActive = 1 AND startTime < :thresholdTime")
    suspend fun markExpiredSessionsAsEnded(now: Long, thresholdTime: Long): Int
    
    /**
     * 删除所有会话（用于测试或清空数据）
     */
    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()
    
    /**
     * 删除指定时间之前的会话（清理旧数据）
     * 
     * @param beforeTime 时间戳
     * @return 删除的记录数
     */
    @Query("DELETE FROM focus_sessions WHERE endTime IS NOT NULL AND endTime < :beforeTime")
    suspend fun deleteOldSessions(beforeTime: Long): Int
    
    /**
     * 统计今日会话数量
     * 
     * @param startOfDay 今日开始时间戳
     * @return 会话数量
     */
    @Query("SELECT COUNT(*) FROM focus_sessions WHERE startTime >= :startOfDay")
    suspend fun countTodaySessions(startOfDay: Long): Int
    
    /**
     * 统计今日专注总时长（秒）
     * 
     * @param startOfDay 今日开始时间戳
     * @return 总时长（秒）
     */
    @Query("SELECT SUM(CASE WHEN endTime IS NOT NULL THEN (endTime - startTime) / 1000 ELSE (:currentTime - startTime) / 1000 END) FROM focus_sessions WHERE startTime >= :startOfDay")
    suspend fun sumTodayDuration(startOfDay: Long, currentTime: Long = System.currentTimeMillis()): Int?
}
