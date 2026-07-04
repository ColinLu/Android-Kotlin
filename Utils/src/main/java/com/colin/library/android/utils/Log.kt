package com.colin.library.android.utils

import android.util.Log
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-13
 *
 * Des   :简化日志输出工具类，支持自动获取调用位置信息
 */
object Log {
    private const val VM_STACK = "VMStack.java"

    /**
     * VERBOSE级别日志
     *
     * @param msg 日志消息
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun v(msg: Any?, tag: String? = null) = print(Log.VERBOSE, tag ?: UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * DEBUG级别日志
     *
     * @param msg 日志消息
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun d(msg: Any?, tag: String? = null) = print(Log.DEBUG, tag ?: UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * INFO级别日志
     *
     * @param msg 日志消息
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun i(msg: Any?, tag: String? = null) = print(Log.INFO, tag ?: UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * WARN级别日志
     *
     * @param msg 日志消息
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun w(msg: Any?, tag: String? = null) = print(Log.WARN, tag ?: UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * ERROR级别日志
     *
     * @param msg 日志消息
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun e(msg: Any?, tag: String? = null) = print(Log.ERROR, tag ?: UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * ASSERT级别日志
     *
     * @param msg 日志消息
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun a(msg: Any?, tag: String? = null) = print(Log.ASSERT, tag ?: UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * 格式化输出JSON日志
     *
     * @param json JSON对象、数组或字符串
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun json(json: Any?, tag: String? = null) = print(
        UtilHelper.getUtilConfig().getLogLevel(),
        tag ?: UtilHelper.getUtilConfig().getLogTag(),
        FormatUtil.formatJson(json)
    )

    /**
     * 格式化输出XML日志
     *
     * @param xml XML字符串
     * @param tag 日志标签（可选）
     */
    @JvmStatic
    @JvmOverloads
    fun xml(xml: String?, tag: String? = null) = print(
        UtilHelper.getUtilConfig().getLogLevel(),
        tag ?: UtilHelper.getUtilConfig().getLogTag(),
        FormatUtil.formatXml(xml)
    )

    /**
     * 输出异常堆栈信息
     *
     * @param t 异常对象
     */
    @JvmStatic
    fun log(t: Throwable?) =
        print(Log.ERROR, UtilHelper.getUtilConfig().getLogTag(), Log.getStackTraceString(t))

    /**
     * 通用日志输出
     *
     * @param msg 日志消息
     */
    @JvmStatic
    fun log(msg: Any?) =
        print(UtilHelper.getUtilConfig().getLogLevel(), UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * 内部打印方法，根据日志级别和配置决定是否输出
     *
     * @param level 日志级别
     * @param tag 日志标签
     * @param msg 日志消息
     * @return 打印结果，如果未打印则返回INVALID
     */
    private fun print(level: Int, tag: String?, msg: Any?): Int {
        if (!UtilHelper.getUtilConfig().isShowLog() || level < UtilHelper.getUtilConfig()
                .getLogLevel()
        ) return INVALID
        val logTag = tag ?: getTag(Thread.currentThread().stackTrace)
        return when (level) {
            Log.VERBOSE -> android.util.Log.v(logTag, "$msg")
            Log.DEBUG -> android.util.Log.d(logTag, "$msg")
            Log.INFO -> android.util.Log.i(logTag, "$msg")
            Log.WARN -> android.util.Log.w(logTag, "$msg")
            Log.ERROR -> android.util.Log.e(logTag, "$msg")
            Log.ASSERT -> android.util.Log.wtf(logTag, "$msg")
            else -> INVALID
        }
    }

    /**
     * 从堆栈轨迹中获取调用者信息作为日志Tag
     *
     * @param traces 堆栈轨迹数组
     * @return 文件名:行号格式的Tag
     */
    private fun getTag(traces: Array<StackTraceElement>): String {
        val index = if (traces.getOrNull(0)?.fileName == VM_STACK) 4 else 3
        return classInfo(traces.getOrElse(index) { traces[3] })
    }

    /**
     * 从堆栈元素中提取类信息
     *
     * @param element 堆栈元素
     * @return 文件名:行号格式的字符串
     */
    private fun classInfo(element: StackTraceElement): String {
        return "${element.fileName}:${element.lineNumber}"
    }
}
