package com.colin.library.android.utils

import android.util.Log
import com.colin.library.android.utils.Log.json
import com.colin.library.android.utils.Log.xml
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
     */
    @JvmStatic
    fun v(msg: Any?) = print(Log.VERBOSE, UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * VERBOSE级别日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param msg 日志消息
     */
    @JvmStatic
    fun v(tag: String, msg: Any?) = print(Log.VERBOSE, tag, msg)

    /**
     * DEBUG级别日志
     *
     * @param msg 日志消息
     */
    @JvmStatic
    fun d(msg: Any?) = print(Log.DEBUG, UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * DEBUG级别日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param msg 日志消息
     */
    @JvmStatic
    fun d(tag: String, msg: Any?) = print(Log.DEBUG, tag, msg)

    /**
     * INFO级别日志
     *
     * @param msg 日志消息
     */
    @JvmStatic
    fun i(msg: Any?) = print(Log.INFO, UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * INFO级别日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param msg 日志消息
     */
    @JvmStatic
    fun i(tag: String, msg: Any?) = print(Log.INFO, tag, msg)

    /**
     * WARN级别日志
     *
     * @param msg 日志消息
     */
    @JvmStatic
    fun w(msg: Any?) = print(Log.WARN, UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * WARN级别日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param msg 日志消息
     */
    @JvmStatic
    fun w(tag: String, msg: Any?) = print(Log.WARN, tag, msg)

    /**
     * ERROR级别日志
     *
     * @param msg 日志消息
     */
    @JvmStatic
    fun e(msg: Any?) = print(Log.ERROR, UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * ERROR级别日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param msg 日志消息
     */
    @JvmStatic
    fun e(tag: String, msg: Any?) = print(Log.ERROR, tag, msg)

    /**
     * ASSERT级别日志
     *
     * @param msg 日志消息
     */
    @JvmStatic
    fun a(msg: Any?) = print(Log.ASSERT, UtilHelper.getUtilConfig().getLogTag(), msg)

    /**
     * ASSERT级别日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param msg 日志消息
     */
    @JvmStatic
    fun a(tag: String, msg: Any?) = print(Log.ASSERT, tag, msg)

    /**
     * 格式化输出JSON日志
     *
     * @param json JSON对象、数组或字符串
     */
    @JvmStatic
    fun json(json: Any?) = print(
        UtilHelper.getUtilConfig().getLogLevel(),
        UtilHelper.getUtilConfig().getLogTag(),
        FormatUtil.formatJson(json)
    )

    /**
     * 格式化输出JSON日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param json JSON对象、数组或字符串
     */
    @JvmStatic
    fun json(tag: String, json: Any?) =
        print(UtilHelper.getUtilConfig().getLogLevel(), tag, FormatUtil.formatJson(json))

    /**
     * 格式化输出XML日志
     *
     * @param xml XML字符串
     */
    @JvmStatic
    fun xml(xml: String?) = print(
        UtilHelper.getUtilConfig().getLogLevel(),
        UtilHelper.getUtilConfig().getLogTag(),
        FormatUtil.formatXml(xml)
    )

    /**
     * 格式化输出XML日志（自定义Tag）
     *
     * @param tag 日志标签
     * @param xml XML字符串
     */
    @JvmStatic
    fun xml(tag: String, xml: String?) =
        print(UtilHelper.getUtilConfig().getLogLevel(), tag, FormatUtil.formatXml(xml))

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
            Log.VERBOSE -> Log.v(logTag, "$msg")
            Log.DEBUG -> Log.d(logTag, "$msg")
            Log.INFO -> Log.i(logTag, "$msg")
            Log.WARN -> Log.w(logTag, "$msg")
            Log.ERROR -> Log.e(logTag, "$msg")
            Log.ASSERT -> Log.wtf(logTag, "$msg")
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
