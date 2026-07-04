package com.colin.nfc.focus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * NFC 标签实体
 * 对应功能：NFC触发、场景配置
 */
@Entity(tableName = "nfc_tags")
data class NfcTag(
    @PrimaryKey
    val uid: String,                    // NFC标签唯一ID
    val name: String,                   // 标签名称（如"工作模式卡"）
    val sceneId: Long?,                 // 关联的场景ID
    val useCount: Int = 0,              // 使用次数统计
    val lastUsedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
