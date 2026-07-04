package com.colin.library.android.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.SparseArray
import androidx.core.content.edit
import com.colin.library.android.utils.helper.UtilHelper
import java.util.concurrent.ConcurrentHashMap

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-24 10:36
 *
 * Des   :SharedPreferences 工具类，支持多文件缓存
 */
object SpUtil {
    private const val DEFAULT_SP_NAME = "app_sp"
    private val SP_CACHE = ConcurrentHashMap<String, SparseArray<SharedPreferences>>()

    /**
     * 存值 (异步 apply)
     */
    @JvmStatic
    @JvmOverloads
    fun put(
        key: String,
        value: Any,
        name: String = DEFAULT_SP_NAME,
        mode: Int = Context.MODE_PRIVATE,
    ) {
        getSp(name, mode).edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                is Set<*> -> @Suppress("UNCHECKED_CAST") putStringSet(key, value as Set<String>)
                else -> Log.e("SpUtil", "Unsupported value type for key: $key")
            }
        }
    }

    /**
     * 存值 (同步 commit)
     */
    @JvmStatic
    @JvmOverloads
    fun commit(
        key: String,
        value: Any,
        name: String = DEFAULT_SP_NAME,
        mode: Int = Context.MODE_PRIVATE,
    ) {
        getSp(name, mode).edit(commit = true) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                is Set<*> -> @Suppress("UNCHECKED_CAST") putStringSet(key, value as Set<String>)
                else -> Log.e("SpUtil", "Unsupported value type for key: $key")
            }
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getBoolean(key: String, def: Boolean = false, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).getBoolean(key, def)

    @JvmStatic
    @JvmOverloads
    fun getInt(key: String, def: Int = 0, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).getInt(key, def)

    @JvmStatic
    @JvmOverloads
    fun getFloat(key: String, def: Float = 0F, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).getFloat(key, def)

    @JvmStatic
    @JvmOverloads
    fun getLong(key: String, def: Long = 0L, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).getLong(key, def)

    @JvmStatic
    @JvmOverloads
    fun getString(key: String, def: String? = null, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).getString(key, def)

    @JvmStatic
    @JvmOverloads
    fun getSet(key: String, def: Set<String>? = null, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).getStringSet(key, def)

    @JvmStatic
    @JvmOverloads
    fun contains(key: String, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) =
        getSp(name, mode).contains(key)

    @JvmStatic
    @JvmOverloads
    fun remove(key: String, name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) {
        getSp(name, mode).edit { remove(key) }
    }

    @JvmStatic
    @JvmOverloads
    fun clear(name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE) {
        getSp(name, mode).edit { clear() }
    }

    /**
     * 获取 SharedPreferences 实例（带缓存）
     */
    fun getSp(name: String = DEFAULT_SP_NAME, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
        val array = SP_CACHE.getOrPut(name) { SparseArray() }
        return array.get(mode) ?: synchronized(array) {
            array.get(mode) ?: UtilHelper.getApplication().getSharedPreferences(name, mode).also {
                array.put(mode, it)
            }
        }
    }
}
