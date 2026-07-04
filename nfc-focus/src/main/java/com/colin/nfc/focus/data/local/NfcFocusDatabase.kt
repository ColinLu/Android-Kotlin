package com.colin.nfc.focus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.colin.nfc.focus.data.model.AlertHistoryEntity
import com.colin.nfc.focus.data.model.FocusScene
import com.colin.nfc.focus.data.model.FocusSessionEntity
import com.colin.nfc.focus.data.model.NfcTag
import com.colin.nfc.focus.data.model.UsageStat

@Database(
    entities = [NfcTag::class, FocusScene::class, UsageStat::class, FocusSessionEntity::class, AlertHistoryEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NfcFocusDatabase : RoomDatabase() {
    
    abstract fun nfcTagDao(): NfcTagDao
    abstract fun focusSceneDao(): FocusSceneDao
    abstract fun usageStatDao(): UsageStatDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun alertHistoryDao(): AlertHistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: NfcFocusDatabase? = null
        
        fun getDatabase(context: Context): NfcFocusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NfcFocusDatabase::class.java,
                    "nfc_focus_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
