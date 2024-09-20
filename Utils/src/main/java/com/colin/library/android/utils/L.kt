package com.colin.library.android.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-25
 * Des   :可控制边框美化,可控制打印等级,可控制Log info, 自动识别类名为Tag, 同时支持自定义Tag的日志工具库
 */
object L {
    private const val TOP_CORNER: Char = '┌'
    private const val BOTTOM_CORNER: Char = '└'
    private const val MIDDLE_CORNER: Char = '├'
    private const val HORIZONTAL_LINE: Char = '│'
    private const val DOUBLE_DIVIDER: String = "──────────────────────────────────────────────"
    private const val SINGLE_DIVIDER: String = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
    private const val TOP_BORDER: String = "$TOP_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"
    private const val MIDDLE_BORDER: String = "$MIDDLE_CORNER$SINGLE_DIVIDER$SINGLE_DIVIDER"
    private const val BOTTOM_BORDER: String = "$BOTTOM_CORNER$DOUBLE_DIVIDER$DOUBLE_DIVIDER"

    //解决windows和linux换行不一致的问题 功能和"\n"是一致的,但是此种写法屏蔽了 Windows和Linux的区别 更保险.
    private val LINE_SEPARATOR = System.lineSeparator()
    private val NULL = "null"
    private var logEnabled = true           // log总开关
    private var logTag: String = "Colin"    // log Tag
    private var logLevel = Log.INFO         // log Level
    private var logThread = true            // log print thread msg
    private var logMethodCount = 1          // log print method count
    private var logMethodOffset = 0         // log print method offset
    private var logMethod = true            // log print method msg
    fun v(msg: Any?) {
        print(Log.VERBOSE, logTag, "$msg")
    }

    fun v(tag: String, msg: Any?) {
        print(Log.VERBOSE, tag, "$msg")
    }

    fun d(msg: Any?) {
        print(Log.DEBUG, logTag, "$msg")
    }

    fun d(tag: String, msg: Any?) {
        print(Log.DEBUG, tag, "$msg")
    }

    fun i(msg: Any?) {
        print(Log.INFO, logTag, "$msg")
    }

    fun i(tag: String, msg: Any?) {
        print(Log.INFO, tag, "$msg")
    }

    fun a(msg: Any?) {
        print(Log.ASSERT, logTag, "$msg")
    }

    fun a(tag: String, msg: Any?) {
        print(Log.ASSERT, tag, "$msg")
    }

    fun e(msg: Any?) {
        print(Log.ERROR, logTag, "$msg")
    }

    fun e(tag: String, msg: Any?) {
        print(Log.ERROR, tag, "$msg")
    }

    fun w(msg: Any?) {
        print(Log.WARN, logTag, "$msg")
    }

    fun w(tag: String, msg: Any?) {
        print(Log.WARN, tag, "$msg")
    }

    fun log(error: Throwable) {
        print(Log.ERROR, logTag, format(error))
    }

    fun log(level: Int = logLevel, tag: String, error: Throwable) {
        print(level, tag, format(error))
    }

    fun log(level: Int = logLevel, tag: String, any: Any?) {
        print(level, tag, "$any")
    }

    fun json(level: Int = logLevel, tag: String = logTag, json: Any?) {
        print(level, tag, formatJson(json) ?: "json is error or null")
    }

    fun xml(level: Int = logLevel, tag: String = logTag, json: String?) {
        print(level, tag, formatXml(json) ?: "xml is error or null")
    }

    fun format(error: Throwable): String {
        var t: Throwable? = error
        while (t != null) t = t.cause
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        error.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    fun formatJson(any: Any?): String? {
        if (any == null) return null
        try {
            if (any is JSONObject) return any.toString(4)
            if (any is JSONArray) return any.toString(4)
            val json = "$any"
            if (json.startsWith('{') && json.endsWith('}')) return JSONObject(json).toString(4)
            if (json.startsWith('[') && json.endsWith(']')) return JSONArray(json).toString(4)
            return json
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun formatXml(xml: String?): String? {
        if (xml.isNullOrEmpty()) return null
        try {
            val xmlInput = StreamSource(StringReader(xml))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
            transformer.transform(xmlInput, xmlOutput)
            return xmlOutput.writer.toString().replaceFirst((">").toRegex(), ">$LINE_SEPARATOR")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return xml
    }

    /**
     * 设置入口
     * <code>
     * L.getSettings()
     * .setLogLevel(Log.WARN)
     * .setLogEnable(true);
     * </code>
     *
     * @return
     */
    fun getSettings(): Settings {
        return Settings()
    }

    /**
     * @param type
     * @param tag
     * @param objects
     */
    @Synchronized
    private fun print(level: Int, tag: String, msg: String) {/*两重设定，过滤是否需要打印log*/
        if (!logEnabled || level < logLevel) {
            return
        }
        //打印上面边框线
        Log.println(level, tag, TOP_BORDER)
        //打印线程信息
        if (logThread) {
            Log.println(level, tag, "$HORIZONTAL_LINE${Thread.currentThread().name}")
            Log.println(level, tag, MIDDLE_BORDER)
        }
        //打印头部调用类、方法等信息
        if (printMethod()) {
            val trace = Thread.currentThread().stackTrace
            val count = getMethodCount()
            val offset = getMethodOffset() + 3
            val space = StringBuilder()
            for (i in count downTo 1) {
                val index = i + offset
                if (index >= trace.size) continue
                val element = trace[index]
                Log.println(
                    level,
                    tag,
                    "$HORIZONTAL_LINE$space${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})"
                )
                space.append("\t")
            }
            Log.println(level, tag, MIDDLE_BORDER)
        }
        //打印需要输出的log
        msg.split(LINE_SEPARATOR).forEach {
            Log.println(level, tag, "$HORIZONTAL_LINE$it")
        }
        //打印底部边框
        Log.println(level, tag, BOTTOM_BORDER)
    }

    private fun getMethodCount() = logMethodCount
    private fun getMethodOffset() = logMethodOffset
    private fun printMethod() = logMethod && getMethodCount() > 0


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
        fun setEnabled(enable: Boolean) = apply {
            logEnabled = enable
        }

        /**
         * 设置Log 全局 Tag
         *
         * @param enable
         * @return
         */
        fun setTag(tag: String) = apply {
            logTag = tag
        }

        /**
         * 设置Log是否打印线程信息
         *
         * @param enable
         * @return
         */
        fun setThread(enable: Boolean) = apply {
            logThread = enable
        }

        /**
         * 设置Log是否打印头部信息
         *
         * @param enable
         * @return
         */
        fun setMethod(enable: Boolean) = apply {
            logMethod = enable
        }

        /**
         * 设置打印等级,只有高于该打印等级的log会被打印<br>
         * 打印等级从低到高分别为: Log.VERBOSE < Log.DEBUG < Log.INFO < Log.WARN < Log.ERROR < Log.ASSERT
         *
         * @param level
         */
        fun setLevel(level: Int) = apply {
            logLevel = level
        }

        fun setMethodCount(count: Int) = apply {
            logMethodCount = count
        }

        fun setMethodOffset(offset: Int) = apply {
            logMethodOffset = offset
        }

    }


}