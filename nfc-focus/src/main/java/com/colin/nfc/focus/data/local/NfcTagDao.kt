package com.colin.nfc.focus.data.local

import androidx.room.*
import com.colin.nfc.focus.data.model.NfcTag
import kotlinx.coroutines.flow.Flow

@Dao
interface NfcTagDao {
    
    @Query("SELECT * FROM nfc_tags ORDER BY lastUsedAt DESC")
    suspend fun getAllNfcTags(): List<NfcTag>
    
    @Query("SELECT * FROM nfc_tags WHERE sceneId = :sceneId")
    suspend fun getTagsBySceneId(sceneId: Long): List<NfcTag>
    
    @Query("SELECT * FROM nfc_tags WHERE uid = :uid LIMIT 1")
    suspend fun getTagByUid(uid: String): NfcTag?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNfcTag(tag: NfcTag): Long
    
    @Update
    suspend fun updateNfcTag(tag: NfcTag)
    
    @Delete
    suspend fun deleteNfcTag(tag: NfcTag)
    
    @Query("DELETE FROM nfc_tags WHERE uid = :uid")
    suspend fun deleteNfcTagByUid(uid: String)
    
    @Query("UPDATE nfc_tags SET lastUsedAt = :timestamp, useCount = useCount + 1 WHERE uid = :uid")
    suspend fun updateUsage(uid: String, timestamp: Long = System.currentTimeMillis())
}
