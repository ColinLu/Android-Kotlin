package com.colin.nfc.focus.data.local

import androidx.room.*
import com.colin.nfc.focus.data.model.FocusScene
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSceneDao {
    
    @Query("SELECT * FROM focus_scenes ORDER BY createdAt DESC")
    suspend fun getAllFocusScenes(): List<FocusScene>
    
    @Query("SELECT * FROM focus_scenes WHERE isActive = 1")
    suspend fun getActiveFocusScenes(): List<FocusScene>
    
    @Query("SELECT * FROM focus_scenes WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveScene(): FocusScene?
    
    @Query("SELECT * FROM focus_scenes WHERE id = :id LIMIT 1")
    suspend fun getSceneById(id: Long): FocusScene?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusScene(scene: FocusScene): Long
    
    @Update
    suspend fun updateFocusScene(scene: FocusScene)
    
    @Delete
    suspend fun deleteFocusScene(scene: FocusScene)
    
    @Query("DELETE FROM focus_scenes WHERE id = :id")
    suspend fun deleteFocusSceneById(id: Long)
    
    @Query("UPDATE focus_scenes SET isActive = 0")
    suspend fun deactivateAllScenes()
    
    @Query("UPDATE focus_scenes SET isActive = 1 WHERE id = :id")
    suspend fun activateScene(id: Long)
}
