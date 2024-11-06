package com.colin.library.android.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.SparseArray
import androidx.collection.ArrayMap
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-07
 *
 * Des   :sp工具类
 */
class SpUtil private constructor() {
    init {
        throw UnsupportedOperationException("don't instantiate")
    }

    companion object {
        private const val SP_NAME = "app_sp"
        private val SP_MAP = ArrayMap<String, SparseArray<SharedPreferences>>()

        fun put(key: String, value: Any): Boolean {
            return put(SP_NAME, Context.MODE_PRIVATE, key, value, false)
        }

        fun put(key: String, value: Any, commit: Boolean): Boolean {
            return put(SP_NAME, Context.MODE_PRIVATE, key, value, commit)
        }

        fun put(
            spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String, value: Any
        ): Boolean {
            return put(spName, mode, key, value, false)
        }

        fun put(key: String, value: Set<String?>) {
            put(SP_NAME, Context.MODE_PRIVATE, key, value, false)
        }

        fun put(key: String, value: Set<String?>, commit: Boolean) {
            put(SP_NAME, Context.MODE_PRIVATE, key, value, commit)
        }

        private fun put(
            spName: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE,
            key: String,
            value: Set<String>,
            commit: Boolean = false
        ): Boolean {
            if (commit) return getSp(spName, mode).edit().putStringSet(key, value).commit()
            else getSp(spName, mode).edit().putStringSet(key, value).apply()
            return false
        }

        /**
         * 存值
         *
         * @param spName SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @param value  存值 一定要区分Number 类型
         * @param commit 提交方式
         */
        private fun put(
            spName: String,
            mode: Int = Context.MODE_PRIVATE,
            key: String,
            value: Any,
            commit: Boolean = false
        ): Boolean {
            when (value) {
                is Boolean -> {
                    if (commit) return getSp(spName, mode).edit().putBoolean(key, value).commit()
                    else getSp(spName, mode).edit().putBoolean(key, value).apply()
                    return true
                }

                is Int -> {
                    if (commit) return getSp(spName, mode).edit().putInt(key, value).commit()
                    else getSp(spName, mode).edit().putInt(key, value).apply()
                    return true
                }

                is Float -> {
                    if (commit) return getSp(spName, mode).edit().putFloat(key, value).commit()
                    else getSp(spName, mode).edit().putFloat(key, value).apply()
                    return true
                }

                is Long -> {
                    if (commit) return getSp(spName, mode).edit().putLong(key, value).commit()
                    else getSp(spName, mode).edit().putLong(key, value).apply()
                    return true
                }

                is String -> {
                    if (commit) return getSp(spName, mode).edit().putString(key, value).commit()
                    else getSp(spName, mode).edit().putString(key, value).apply()
                    return true
                }

                else -> return false
            }
        }

        /*String*/
        fun getString(key: String, def: String? = null): String? {
            return getSp(SP_NAME, Context.MODE_PRIVATE).getString(key, def)
        }

        fun getString(
            spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String, def: String?
        ): String? {
            return getSp(spName, mode).getString(key, def)
        }


        /*Boolean*/
        fun getBoolean(key: String, def: Boolean = false): Boolean {
            return getSp(SP_NAME, Context.MODE_PRIVATE).getBoolean(key, def)
        }


        fun getBoolean(
            spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String, def: Boolean
        ): Boolean {
            return getSp(spName, mode).getBoolean(key, def)
        }


        /*Integer*/
        fun getInt(key: String, def: Int = 0): Int {
            return getSp(SP_NAME, Context.MODE_PRIVATE).getInt(key, def)
        }

        fun getInt(
            spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String, def: Int
        ): Int {
            return getSp(spName, mode).getInt(key, def)
        }

        /*Float*/
        fun getFloat(key: String, def: Float = 0f): Float {
            return getSp(SP_NAME, Context.MODE_PRIVATE).getFloat(key, def)
        }

        fun getFloat(
            spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String, def: Float
        ): Float {
            return getSp(spName, mode).getFloat(key, def)
        }


        /*Long*/
        fun getLong(key: String, def: Long = 0L): Long {
            return getSp(SP_NAME, Context.MODE_PRIVATE).getLong(key, def)
        }


        fun getLong(
            spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String, def: Long
        ): Long {
            return getSp(spName, mode).getLong(key, def)
        }


        /*set*/
        fun getSet(key: String, def: Set<String> = emptySet()): Set<String> {
            return getSp(SP_NAME, Context.MODE_PRIVATE).getStringSet(key, def) ?: emptySet()
        }

        fun getSet(
            spName: String = SP_NAME,
            mode: Int = Context.MODE_PRIVATE,
            key: String,
            def: Set<String> = emptySet()
        ): Set<String> {
            return getSp(spName, mode).getStringSet(key, def) ?: emptySet()
        }


        /*set*/
        fun contains(key: String): Boolean {
            return contains(SP_NAME, Context.MODE_PRIVATE, key)
        }

        fun contains(spName: String, key: String): Boolean {
            return contains(spName, Context.MODE_PRIVATE, key)
        }

        /**
         * 查询Sp 是否保存某一个关键词
         *
         * @param spName SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @return 返回 true 含有 关键词  false  不含有 也可能 获取失败
         */
        fun contains(spName: String, mode: Int, key: String): Boolean {
            return getSp(spName, mode).contains(key)
        }


        fun remove(key: String) {
            remove(SP_NAME, Context.MODE_PRIVATE, key)
        }

        fun remove(spName: String, key: String) {
            remove(spName, Context.MODE_PRIVATE, key)
        }

        /**
         * 移除
         *
         * @param spName SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    移除关键字
         */
        @JvmStatic
        fun remove(spName: String, mode: Int, key: String) {
            getSp(spName, mode).edit().remove(key).apply()
        }

        /**
         * 清除
         *
         * @param spName SP 本地保存文件名
         * @param mode   SP 保存模式
         */
        @JvmStatic
        fun clear(spName: String = SP_NAME, mode: Int = Context.MODE_PRIVATE) {
            getSp(spName, mode).edit().clear().apply()
        }

        @JvmStatic
        fun getSp(spName: String, mode: Int): SharedPreferences {
            var sparseArray = SP_MAP[spName]
            if (sparseArray == null) {
                sparseArray = SparseArray()
                val preferences = UtilHelper.getApplication().getSharedPreferences(spName, mode)
                sparseArray.put(mode, preferences)
                SP_MAP[spName] = sparseArray
                return preferences
            } else {
                var preferences = sparseArray[mode]
                if (null == preferences) {
                    preferences = UtilHelper.getApplication().getSharedPreferences(spName, mode)
                    sparseArray.put(mode, preferences)
                }
                return preferences
            }
        }
    }
}