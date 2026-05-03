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
 * Des   :SpUtil
 */
class SpUtil private constructor() {
    init {
        throw UnsupportedOperationException("don't instantiate")
    }

    companion object {
        private const val SP_NAME = "app_sp"
        private val SP_MAP = ConcurrentHashMap<String, SparseArray<SharedPreferences>>()

        /**
         * 存值
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @param value  存值 一定要区分Number 类型
         * @return apply 提交方式
         */
        @JvmStatic
        @JvmOverloads
        fun put(
            key: String,
            value: Any,
            name: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE,
        ) {
            when (value) {
                is Boolean -> getSp(name, mode).edit { putBoolean(key, value) }
                is Int -> getSp(name, mode).edit { putInt(key, value) }
                is Float -> getSp(name, mode).edit { putFloat(key, value) }
                is Long -> getSp(name, mode).edit { putLong(key, value) }
                is String -> getSp(name, mode).edit { putString(key, value) }
                else -> {
                    Log.e("key:$key value:$value name:$name,mode:$mode not support")
                }
            }
        }

        /**
         * 存值
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @param value  存值 一定要区分Number 类型
         * @return commit 提交方式
         */
        @JvmStatic
        @JvmOverloads
        fun commit(
            key: String,
            value: Any,
            name: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE,
        ) {
            when (value) {
                is Boolean -> getSp(name, mode).edit(commit = true) { putBoolean(key, value) }
                is Int -> getSp(name, mode).edit(commit = true) { putInt(key, value) }
                is Float -> getSp(name, mode).edit(commit = true) { putFloat(key, value) }
                is Long -> getSp(name, mode).edit(commit = true) { putLong(key, value) }
                is String -> getSp(name, mode).edit(commit = true) { putString(key, value) }
                else -> {
                    Log.e("key:$key value:$value name:$name,mode:$mode not support")
                }
            }
        }

        @JvmStatic
        @JvmOverloads
        fun getBoolean(
            key: String,
            def: Boolean = false,
            name: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).getBoolean(key, def)


        @JvmStatic
        @JvmOverloads
        fun getInt(
            key: String, def: Int = 0, name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).getInt(key, def)


        @JvmStatic
        @JvmOverloads
        fun getFloat(
            key: String, def: Float = 0F, name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).getFloat(key, def)

        @JvmStatic
        @JvmOverloads
        fun getLong(
            key: String, def: Long = 0L, name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).getLong(key, def)

        @JvmStatic
        @JvmOverloads
        fun getString(
            key: String,
            def: String? = null,
            name: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).getString(key, def)

        @JvmStatic
        @JvmOverloads
        fun getSet(
            key: String,
            def: Set<String>? = null,
            name: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).getStringSet(key, def)

        /**
         * 查询SharedPreferences中是否包含指定key
         *
         * @param key 关键字
         * @param name SP文件名
         * @param mode SP保存模式
         * @return true表示包含该key，false表示不包含
         */
        @JvmStatic
        @JvmOverloads
        fun contains(
            key: String, name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE
        ) = getSp(name, mode).contains(key)


        /**
         * 移除指定key的值
         *
         * @param key 移除关键字
         * @param name SP文件名
         * @param mode SP保存模式
         */
        @JvmStatic
        @JvmOverloads
        fun remove(key: String, name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE) {
            getSp(name, mode).edit { remove(key) }
        }


        /**
         * 清空指定SP文件的所有数据
         *
         * @param name SP文件名
         * @param mode SP保存模式
         */
        @JvmStatic
        @JvmOverloads
        fun clear(name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE) {
            getSp(name, mode).edit { clear() }
        }


        /**
         * 获取SharedPreferences实例（带缓存）
         *
         * @param name SP文件名
         * @param mode SP保存模式
         * @return SharedPreferences实例
         */
        @Synchronized
        fun getSp(name: String, mode: Int): SharedPreferences {
            var array = SP_MAP[name]
            if (array == null) {
                array = SparseArray<SharedPreferences>()
                val preferences: SharedPreferences = UtilHelper.getApplication().getSharedPreferences(name, mode)
                array.put(mode, preferences)
                SP_MAP[name] = array
                return preferences
            } else {
                var preferences = array.get(mode)
                if (null == preferences) {
                    preferences = UtilHelper.getApplication().getSharedPreferences(name, mode)
                    array.put(mode, preferences)
                }
                return preferences!!
            }
        }
    }
}