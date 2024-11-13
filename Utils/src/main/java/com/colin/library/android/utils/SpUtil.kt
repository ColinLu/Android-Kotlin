package com.colin.library.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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

        fun commit(key: String, value: Any): Boolean {
            return commit(SP_NAME, Context.MODE_PRIVATE, key, value)
        }

        fun apply(key: String, value: Any) {
            apply(SP_NAME, Context.MODE_PRIVATE, key, value)
        }

        /**
         * sp 存值 commit 提交方式-->>主线程，返回设置结果
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @param value  存值 一定要区分Number 类型
         * @return 设置结果
         */
        @SuppressLint("ApplySharedPref")
        public fun commit(
            name: String, mode: Int = Context.MODE_PRIVATE, key: String, value: Any
        ): Boolean {
            return when (value) {
                is Int -> getSp(name, mode).edit().putInt(key, value).commit()
                is Long -> getSp(name, mode).edit().putLong(key, value).commit()
                is Float -> getSp(name, mode).edit().putFloat(key, value).commit()
                is String -> getSp(name, mode).edit().putString(key, value).commit()
                is Boolean -> getSp(name, mode).edit().putBoolean(key, value).commit()
                else -> throw UnsupportedOperationException("not support about any $value")
            }
        }

        /**
         * sp 存值 apply 提交方式-->>子线程，不返回设置结果
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @param value  存值 一定要区分Number 类型
         */
        public fun apply(name: String, mode: Int = Context.MODE_PRIVATE, key: String, value: Any) {
            when (value) {
                is Int -> getSp(name, mode).edit().putInt(key, value).apply()
                is Long -> getSp(name, mode).edit().putLong(key, value).apply()
                is Float -> getSp(name, mode).edit().putFloat(key, value).apply()
                is String -> getSp(name, mode).edit().putString(key, value).apply()
                is Boolean -> getSp(name, mode).edit().putBoolean(key, value).apply()
                else -> throw UnsupportedOperationException("not support about any $value")
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
        fun contains(key: String): Boolean {
            return contains(SP_NAME, Context.MODE_PRIVATE, key)
        }

        /**
         * 查询Sp 是否保存某一个关键词
         *
         * @param spName SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    关键字
         * @return 返回 true 含有 关键词  false  不含有 也可能 获取失败
         */
        fun contains(
            name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String
        ): Boolean {
            return getSp(name, mode).contains(key)
        }

        @JvmStatic
        fun remove(key: String) {
            remove(SP_NAME, Context.MODE_PRIVATE, key)
        }

        /**
         * 移除
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         * @param key    移除关键字
         */
        @JvmStatic
        fun remove(name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE, key: String) {
            getSp(name, mode).edit().remove(key).apply()
        }

        /**
         * 清除
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         */
        @JvmStatic
        fun clear(name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE) {
            getSp(name, mode).edit().clear().apply()
        }

        @JvmStatic
        fun getSp(name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
            val sparseArray = SP_MAP[name] ?: SparseArray<SharedPreferences>().also {
                SP_MAP[name] = it
            }
            val preferences = sparseArray[mode] ?: UtilHelper.getSp(name, mode).also {
                sparseArray.put(mode, it)
            }
            return preferences
        }

        /**
         * 获取Editor
         *
         * @param name SP 本地保存文件名
         * @param mode   SP 保存模式
         */
        @JvmStatic
        fun getEdit(name: String = SP_NAME, mode: Int = Context.MODE_PRIVATE): Editor =
            getSp(name, mode).edit()


    }
}