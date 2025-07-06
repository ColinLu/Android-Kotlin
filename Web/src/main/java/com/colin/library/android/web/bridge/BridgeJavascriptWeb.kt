package com.colin.library.android.web.bridge

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-01 13:10
 *
 * Des   :BridgeJavascriptWeb
 */
interface BridgeJavascriptWeb {
    /*注册js 方法 单个*/
    fun registerHandler(handlerName: String, handler: BridgeHandler)

    /*注册js 方法 集合*/
    fun registerHandler(handlerNames: List<String>, handler: BridgeHandler)

    /*发送信息 给js*/
    fun send(data: String?)

    /*发送信息 给js*/
    fun send(data: String?, callback: BridgeCallback?)

    fun callHandler(handlerName: String?, data: String?, callBack: BridgeCallback?)

    /*轮训 获取 js 返回内容*/
    fun handlerReturnData(url: String)

    /*遍历 js消息反馈给Android 原生*/
    fun flushMessageQueue()

    /*分发message 必须在主线程才分发成功*/
    fun dispatchMessage(message: BridgeMessage)

    fun getMessage(): MutableList<BridgeMessage>?

    fun setMessage(list: MutableList<BridgeMessage>?)

    /*解绑 js方法*/
    fun unregisterHandler(handlerName: String)
}