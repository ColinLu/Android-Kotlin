package com.colin.nfc.focus.nfc

import com.colin.nfc.focus.data.local.NfcFocusDatabase
import com.colin.nfc.focus.data.model.NfcTag
import com.colin.nfc.focus.util.e
import com.colin.nfc.focus.util.i
import com.colin.nfc.focus.util.w
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * NFC 标签工具类
 * 
 * 提供标签管理的高级功能：
 * - 标签与场景绑定
 * - 标签信息查询
 * - 标签历史记录
 */
class NfcTagHelper(private val database: NfcFocusDatabase) {

    companion object {
        private const val TAG = "NfcTagHelper"
    }

    /**
     * 根据 UID 获取标签信息
     * 
     * @param uid 标签 UID
     * @return 标签信息或 null
     */
    suspend fun getTagByUid(uid: String): NfcTag? {
        return withContext(Dispatchers.IO) {
            database.nfcTagDao().getTagByUid(uid)
        }
    }

    /**
     * 注册新标签
     * 
     * @param uid 标签 UID
     * @param name 标签名称
     * @param sceneId 关联的场景 ID（可选）
     * @return 注册的标签
     */
    suspend fun registerTag(
        uid: String,
        name: String,
        sceneId: Long? = null,
    ): NfcTag {
        return withContext(Dispatchers.IO) {
            val tag = NfcTag(
                uid = uid,
                name = name,
                sceneId = sceneId,
                createdAt = System.currentTimeMillis(),
                lastUsedAt = null,
                useCount = 0
            )

            database.nfcTagDao().insertNfcTag(tag)
            tag
        }
    }

    /**
     * 更新标签信息
     * 
     * @param tag 更新后的标签
     */
    suspend fun updateTag(tag: NfcTag) {
        withContext(Dispatchers.IO) {
            database.nfcTagDao().updateNfcTag(tag)
        }
    }

    /**
     * 删除标签
     * 
     * @param uid 标签 UID
     */
    suspend fun deleteTag(uid: String) {
        withContext(Dispatchers.IO) {
            database.nfcTagDao().deleteNfcTagByUid(uid)
        }
    }

    /**
     * 将标签与场景绑定
     * 
     * @param uid 标签 UID
     * @param sceneId 场景 ID
     * @return 是否绑定成功
     */
    suspend fun bindTagToScene(uid: String, sceneId: Long): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val tag = database.nfcTagDao().getTagByUid(uid)

                if (tag != null) {
                    // 更新现有标签
                    val updatedTag = tag.copy(sceneId = sceneId)
                    database.nfcTagDao().updateNfcTag(updatedTag)
                } else {
                    // 创建新标签
                    val newTag = NfcTag(
                        uid = uid,
                        name = "标签-${uid.takeLast(4)}",
                        sceneId = sceneId,
                        createdAt = System.currentTimeMillis()
                    )
                    database.nfcTagDao().insertNfcTag(newTag)
                }
            }
            "标签 $uid 已绑定到场景 $sceneId".i(TAG)
            true
        } catch (e: Exception) {
            "绑定标签失败".e(TAG, e)
            false
        }
    }

    /**
     * 解除标签与场景的绑定
     * 
     * @param uid 标签 UID
     * @return 是否解绑成功
     */
    suspend fun unbindTag(uid: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val tag = database.nfcTagDao().getTagByUid(uid)

                if (tag != null) {
                    val updatedTag = tag.copy(sceneId = null)
                    database.nfcTagDao().updateNfcTag(updatedTag)
                    "标签 $uid 已解绑".i(TAG)
                    true
                } else {
                    "标签 $uid 不存在".w(TAG)
                    false
                }
            }
        } catch (e: Exception) {
            "解绑标签失败".e(TAG, e)
            false
        }
    }

    /**
     * 记录标签使用
     * 
     * @param uid 标签 UID
     */
    suspend fun recordTagUsage(uid: String) {
        withContext(Dispatchers.IO) {
            val tag = database.nfcTagDao().getTagByUid(uid)

            if (tag != null) {
                val updatedTag = tag.copy(
                    lastUsedAt = System.currentTimeMillis(),
                    useCount = tag.useCount + 1,
                )
                database.nfcTagDao().updateNfcTag(updatedTag)
            }
        }
    }

    /**
     * 获取所有已注册的标签
     * 
     * @return 标签列表
     */
    suspend fun getAllTags(): List<NfcTag> {
        return withContext(Dispatchers.IO) {
            database.nfcTagDao().getAllNfcTags()
        }
    }

    /**
     * 获取与场景绑定的标签
     * 
     * @param sceneId 场景 ID
     * @return 标签列表
     */
    suspend fun getTagsForScene(sceneId: Long): List<NfcTag> {
        return withContext(Dispatchers.IO) {
            database.nfcTagDao().getTagsBySceneId(sceneId)
        }
    }
}
