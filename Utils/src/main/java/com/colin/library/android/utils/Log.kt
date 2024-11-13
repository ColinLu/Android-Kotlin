package com.colin.library.android.utils

import android.util.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-13
 *
 * Des   :简化log输出
 * public static final int VERBOSE = 2;
 * public static final int DEBUG = 3;
 * public static final int INFO = 4;
 * public static final int WARN = 5;
 * public static final int ERROR = 6;
 * public static final int ASSERT = 7;
 */
class Log {

    companion object {
        private const val VM_STACK = "VMStack.java"
        private var level: Int = Log.INFO
        fun v(msg: Any?): Int {
            return print(Log.VERBOSE, null, msg)
        }

        fun v(tag: String, msg: Any?): Int {
            return print(Log.VERBOSE, tag, msg)
        }

        fun d(msg: Any?): Int {
            return print(Log.DEBUG, null, msg)
        }

        fun d(tag: String, msg: Any?): Int {
            return print(Log.DEBUG, tag, msg)
        }

        fun i(msg: Any?): Int {
            return print(Log.INFO, null, msg)
        }

        fun i(tag: String, msg: Any?): Int {
            return print(Log.INFO, tag, msg)
        }

        fun w(msg: Any?): Int {
            return print(Log.WARN, null, msg)
        }

        fun w(tag: String, msg: Any?): Int {
            return print(Log.WARN, tag, msg)
        }

        fun e(msg: Any?): Int {
            return print(Log.ERROR, null, msg)
        }

        fun e(tag: String, msg: Any?): Int {
            return print(Log.ERROR, tag, msg)
        }

        fun a(msg: Any?): Int {
            return print(Log.ASSERT, null, msg)
        }

        fun a(tag: String, msg: Any?): Int {
            return print(Log.ASSERT, tag, msg)
        }

        fun json(json: Any?): Int {
            return print(level, null, FormatUtil.formatJson(json))
        }

        fun json(tag: String, json: Any?): Int {
            return print(level, tag, FormatUtil.formatJson(json))
        }

        fun xml(xml: String?): Int {
            return print(level, null, FormatUtil.formatXml(xml))
        }

        fun xml(tag: String, xml: String?): Int {
            return print(level, tag, FormatUtil.formatXml(xml))
        }

        fun log(t: Throwable?): Int {
            return print(Log.ERROR, null, Log.getStackTraceString(t))
        }

        fun log(msg: Any?): Int {
            return print(level, null, msg)
        }


        private fun print(level: Int, tag: String?, msg: Any?): Int {
            val logTag = getTag(tag, Thread.currentThread().stackTrace)
            return Log.println(level, logTag, "$msg")
        }

        private fun getTag(tag: String?, traces: Array<StackTraceElement>): String {
            val index = if (traces.getOrNull(0)?.fileName == VM_STACK) 4 else 3
            return classInfo(tag, traces.getOrElse(index) { traces[3] })
        }

        private fun classInfo(tag: String?, element: StackTraceElement): String {
            return "${tag ?: ""}${element.fileName}:${element.lineNumber}"
        }
    }


}