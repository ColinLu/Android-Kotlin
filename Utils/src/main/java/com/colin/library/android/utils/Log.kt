package com.colin.library.android.utils

import android.util.Log
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-13
 *
 * Des   :简化log输出
 */
object Log {
    private const val VM_STACK = "VMStack.java"

    @JvmStatic
    fun v(msg: Any?) = print(Log.VERBOSE, UtilHelper.getUtilConfig().getLogTag(), msg)

    @JvmStatic
    fun v(tag: String, msg: Any?) = print(Log.VERBOSE, tag, msg)

    @JvmStatic
    fun d(msg: Any?) = print(Log.DEBUG, UtilHelper.getUtilConfig().getLogTag(), msg)

    @JvmStatic
    fun d(tag: String, msg: Any?) = print(Log.DEBUG, tag, msg)

    @JvmStatic
    fun i(msg: Any?) = print(Log.INFO, UtilHelper.getUtilConfig().getLogTag(), msg)

    @JvmStatic
    fun i(tag: String, msg: Any?) = print(Log.INFO, tag, msg)

    @JvmStatic
    fun w(msg: Any?) = print(Log.WARN, UtilHelper.getUtilConfig().getLogTag(), msg)

    @JvmStatic
    fun w(tag: String, msg: Any?) = print(Log.WARN, tag, msg)

    @JvmStatic
    fun e(msg: Any?) = print(Log.ERROR, UtilHelper.getUtilConfig().getLogTag(), msg)

    @JvmStatic
    fun e(tag: String, msg: Any?) = print(Log.ERROR, tag, msg)

    @JvmStatic
    fun a(msg: Any?) = print(Log.ASSERT, UtilHelper.getUtilConfig().getLogTag(), msg)

    @JvmStatic
    fun a(tag: String, msg: Any?) = print(Log.ASSERT, tag, msg)

    @JvmStatic
    fun json(json: Any?) = print(UtilHelper.getUtilConfig().getLogLevel(), UtilHelper.getUtilConfig().getLogTag(), FormatUtil.formatJson(json))

    @JvmStatic
    fun json(tag: String, json: Any?) = print(UtilHelper.getUtilConfig().getLogLevel(), tag, FormatUtil.formatJson(json))

    @JvmStatic
    fun xml(xml: String?) = print(UtilHelper.getUtilConfig().getLogLevel(), UtilHelper.getUtilConfig().getLogTag(), FormatUtil.formatXml(xml))

    @JvmStatic
    fun xml(tag: String, xml: String?) = print(UtilHelper.getUtilConfig().getLogLevel(), tag, FormatUtil.formatXml(xml))

    @JvmStatic
    fun log(t: Throwable?) = print(Log.ERROR, UtilHelper.getUtilConfig().getLogTag(), Log.getStackTraceString(t))

    @JvmStatic
    fun log(msg: Any?) = print(UtilHelper.getUtilConfig().getLogLevel(), UtilHelper.getUtilConfig().getLogTag(), msg)

    private fun print(level: Int, tag: String?, msg: Any?): Int {
        if (!UtilHelper.getUtilConfig().isShowLog() || level < UtilHelper.getUtilConfig().getLogLevel()) return INVALID
        val logTag = tag ?: getTag(Thread.currentThread().stackTrace)
        return when (level) {
            Log.VERBOSE -> Log.v(logTag, "$msg")
            Log.DEBUG -> Log.d(logTag, "$msg")
            Log.INFO -> Log.i(logTag, "$msg")
            Log.WARN -> Log.w(logTag, "$msg")
            Log.ERROR -> Log.e(logTag, "$msg")
            Log.ASSERT -> Log.wtf(logTag, "$msg")
            else -> INVALID
        }
    }

    private fun getTag(traces: Array<StackTraceElement>): String {
        val index = if (traces.getOrNull(0)?.fileName == VM_STACK) 4 else 3
        return classInfo(traces.getOrElse(index) { traces[3] })
    }

    private fun classInfo(element: StackTraceElement): String {
        return "${element.fileName}:${element.lineNumber}"
    }
}