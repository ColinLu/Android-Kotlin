package com.colin.library.android.web.bridge

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-01 13:13
 *
 * Des   :BridgeHandler
 */
interface BridgeHandler {
    fun handler(handlerName: String, data: String?, function: BridgeCallback?)
}

