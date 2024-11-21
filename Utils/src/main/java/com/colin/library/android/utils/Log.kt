package com.colin.library.android.utils

import android.util.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-13
 *
 * Des   :简化log输出
 */
class Log {

    companion object {
        private const val VM_STACK = "VMStack.java"
        private var mEnabled: Boolean = true
        private var mLevel: Int = Log.INFO
        private var mTag: String? = null

        @JvmStatic
        fun v(msg: Any?): Int {
            return print(Log.VERBOSE, mTag, msg)
        }

        @JvmStatic
        fun v(tag: String, msg: Any?): Int {
            return print(Log.VERBOSE, tag, msg)
        }

        @JvmStatic
        fun d(msg: Any?): Int {
            return print(Log.DEBUG, mTag, msg)
        }

        @JvmStatic
        fun d(tag: String, msg: Any?): Int {
            return print(Log.DEBUG, tag, msg)
        }

        @JvmStatic
        fun i(msg: Any?): Int {
            return print(Log.INFO, mTag, msg)
        }

        @JvmStatic
        fun i(tag: String, msg: Any?): Int {
            return print(Log.INFO, tag, msg)
        }

        @JvmStatic
        fun w(msg: Any?): Int {
            return print(Log.WARN, mTag, msg)
        }

        @JvmStatic
        fun w(tag: String, msg: Any?): Int {
            return print(Log.WARN, tag, msg)
        }

        @JvmStatic
        fun e(msg: Any?): Int {
            return print(Log.ERROR, mTag, msg)
        }

        @JvmStatic
        fun e(tag: String, msg: Any?): Int {
            return print(Log.ERROR, tag, msg)
        }

        @JvmStatic
        fun a(msg: Any?): Int {
            return print(Log.ASSERT, mTag, msg)
        }

        @JvmStatic
        fun a(tag: String, msg: Any?): Int {
            return print(Log.ASSERT, tag, msg)
        }

        @JvmStatic
        fun json(json: Any?): Int {
            return print(mLevel, mTag, FormatUtil.formatJson(json))
        }

        @JvmStatic
        fun json(tag: String, json: Any?): Int {
            return print(mLevel, tag, FormatUtil.formatJson(json))
        }

        @JvmStatic
        fun xml(xml: String?): Int {
            return print(mLevel, mTag, FormatUtil.formatXml(xml))
        }

        @JvmStatic
        fun xml(tag: String, xml: String?): Int {
            return print(mLevel, tag, FormatUtil.formatXml(xml))
        }

        @JvmStatic
        fun log(t: Throwable?): Int {
            return print(Log.ERROR, mTag, Log.getStackTraceString(t))
        }

        @JvmStatic
        fun log(msg: Any?): Int {
            return print(mLevel, mTag, msg)
        }


        private fun print(level: Int, tag: String?, msg: Any?): Int {
            if (mEnabled || level < mLevel) return Constants.INVALID
            val logTag = tag ?: getTag(Thread.currentThread().stackTrace)
            return Log.println(level, logTag, "$msg")
        }

        private fun getTag(traces: Array<StackTraceElement>): String {
            val index = if (traces.getOrNull(0)?.fileName == VM_STACK) 4 else 3
            return classInfo(traces.getOrElse(index) { traces[3] })
        }

        private fun classInfo(element: StackTraceElement): String {
            return "${element.fileName}:${element.lineNumber}"
        }
    }

    /**
     * 设置Log配置入口
     * @return
     */
    fun getSettings(): Settings {
        return Settings()
    }

    /**
     * Log设置类
     */
    class Settings {
        /**
         * 设置Log是否开启
         *
         * @param enable
         * @return
         */
        fun setEnabled(enable: Boolean): Settings {
            mEnabled = enable
            return this
        }

        /**
         * 设置Log 全局 Tag
         *
         * @param tag
         * @return
         */
        fun setTag(tag: String?): Settings {
            mTag = tag
            return this
        }


        /**
         * 设置打印等级,只有高于该打印等级的log会被打印<br>
         * 打印等级从低到高分别为: Log.VERBOSE < Log.DEBUG < Log.INFO < Log.WARN < Log.ERROR < Log.ASSERT
         *
         * @param level
         */
        fun setLevel(level: Int): Settings {
            mLevel = level
            return this
        }

    }

}