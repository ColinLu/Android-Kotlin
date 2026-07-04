package com.colin.nfc.focus.data.local

import androidx.room.*
import com.colin.nfc.focus.data.model.UsageStat
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageStatDao {
    
    @Query("SELECT * FROM usage_stats ORDER BY startTime DESC LIMIT :limit")
    fun getRecentStats(limit: Int = 50): Flow<List<UsageStat>>
    
    @Query("SELECT * FROM usage_stats WHERE sceneId = :sceneId ORDER BY startTime DESC")
    fun getStatsByScene(sceneId: Long): Flow<List<UsageStat>>
    
    @Insert
    suspend fun insertUsageStat(stat: UsageStat): Long
    
    @Update
    suspend fun updateUsageStat(stat: UsageStat)
    
    @Query("SELECT * FROM usage_stats WHERE id = :id")
    suspend fun getUsageStatById(id: Long): UsageStat?
    
    @Query("SELECT * FROM usage_stats WHERE startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    suspend fun getUsageStatsByDateRange(startDate: Long, endDate: Long): List<UsageStat>
    
    @Query("DELETE FROM usage_stats")
    suspend fun deleteAllUsageStats()
    
    @Query("""
        SELECT SUM(duration) FROM usage_stats 
        WHERE startTime BETWEEN :startTime AND :endTime
    """)
    suspend fun getTotalDuration(startTime: Long, endTime: Long): Int?
    
    @Query("""
        SELECT COUNT(*) FROM usage_stats 
        WHERE startTime BETWEEN :startTime AND :endTime
    """)
    suspend fun getSessionCount(startTime: Long, endTime: Long): Int
}
