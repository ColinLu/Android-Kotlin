package com.colin.library.android.utils

import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-11-13
 *
 * Des   :各种格式化
 */
object FormatUtil {
    private const val INDENT_SPACES = 4
    private val LINE_SEPARATOR = System.lineSeparator()


    /**
     * 格式化JSON字符串
     *
     * @param json 待格式化的JSON对象、数组或字符串
     * @return 格式化后的JSON字符串，如果输入为null则返回null
     */
    @JvmStatic
    fun formatJson(json: Any?): String? {
        if (json == null) return null
        return when (json) {
            is JSONObject -> json.toString(INDENT_SPACES)
            is JSONArray -> json.toString(INDENT_SPACES)
            is String -> {
                when {
                    isJSONObject(json) -> JSONObject(json).toString(INDENT_SPACES)
                    isJSONArray(json) -> JSONArray(json).toString(INDENT_SPACES)
                    else -> json
                }
            }

            else -> "$json"
        }
    }


    /**
     * 格式化XML字符串
     *
     * @param xml 待格式化的XML字符串
     * @return 格式化后的XML字符串，如果格式化失败则返回原字符串
     */
    @JvmStatic
    fun formatXml(xml: String?): String? {
        if (xml == null) return null
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
     * 判断字符串是否为JSONObject格式
     *
     * @param json 待判断的字符串
     * @return true表示是JSONObject格式，false表示不是
     */
    fun isJSONObject(json: String): Boolean {
        return json.startsWith('{') && json.endsWith('}')
    }

    /**
     * 判断字符串是否为JSONArray格式
     *
     * @param json 待判断的字符串
     * @return true表示是JSONArray格式，false表示不是
     */
    fun isJSONArray(json: String): Boolean {
        return json.startsWith('[') && json.endsWith(']')
    }

}