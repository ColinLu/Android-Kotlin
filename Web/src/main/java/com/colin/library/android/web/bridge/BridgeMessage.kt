package com.colin.library.android.web.bridge

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-01 11:09
 *
 * Des   :BridgeMessage
 */
data class BridgeMessage(
    val handlerName: String?,       //消息名称
    val data: String?,              //消息内容
    var callbackId: String = "",    //回调id
    var responseId: String = "",    //响应id
    var responseData: String = ""   //响应内容
) {

    fun toJson(): String? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(HANDLER_NAME_STR, handlerName)
            jsonObject.put(CALLBACK_ID_STR, callbackId)
            jsonObject.put(RESPONSE_ID_STR, responseId)
            jsonObject.put(RESPONSE_DATA_STR, responseData)
            jsonObject.put(DATA_STR, data)
            return jsonObject.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        private const val HANDLER_NAME_STR: String = "handlerName"
        private const val CALLBACK_ID_STR: String = "callbackId"
        private const val RESPONSE_ID_STR: String = "responseId"
        private const val RESPONSE_DATA_STR: String = "responseData"
        const val DATA_STR: String = "data"

        /**
         * 数据格式转换  json 字符串 消息类
         *
         * @param jsonStr
         * @return
         */
        fun toObject(jsonStr: String): BridgeMessage? {
            return toObject(JSONObject(jsonStr))
        }

        /**
         * 数据格式转换  json 字符串 消息类
         *
         * @param json
         * @return
         */
        fun toObject(json: JSONObject): BridgeMessage? {
            try {
                return BridgeMessage(
                    handlerName = json.optString(HANDLER_NAME_STR, ""),
                    callbackId = json.optString(CALLBACK_ID_STR, ""),
                    responseId = json.optString(RESPONSE_ID_STR, ""),
                    responseData = json.optString(RESPONSE_DATA_STR, ""),
                    data = json.optString(DATA_STR, "")
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }

        fun toList(json: String?): MutableList<BridgeMessage>? {
            json ?: return null
            try {
                val list = ArrayList<BridgeMessage>()
                val jsonArray = JSONArray(json)
                for (i in 0..<jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    toObject(jsonObject)?.also { list.add(it) }
                }
                return list
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}