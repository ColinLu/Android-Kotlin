package com.colin.library.android.web

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.colin.library.android.utils.Log
import com.colin.library.android.web.bridge.BridgeCallback
import com.colin.library.android.web.bridge.BridgeHandler
import com.colin.library.android.web.bridge.BridgeJavascriptWeb
import com.colin.library.android.web.bridge.BridgeMessage
import com.colin.library.android.web.bridge.BridgeUtil
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import java.net.URLEncoder

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-08 12:46
 *
 * Des   :CustomWebView
 */
class X5WebViewDefault @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), BridgeJavascriptWeb, LifecycleEventObserver {
    private val URL_MAX_CHARACTER_NUM: Int = 2097152
    private val HANDLER_DEFAULT = object : BridgeHandler {
        override fun handler(handlerName: String, data: String?, function: BridgeCallback?) {
            Log.i("javascriptEnabled:$javascriptEnabled handlerName:$handlerName data:$data")
            function?.call("DefaultHandler response data")
        }
    }
    private lateinit var settings: WebSettings
    private var messages: MutableList<BridgeMessage>? = null
    private val callbacks: MutableMap<String, BridgeCallback> = HashMap()
    private val handlers: MutableMap<String, BridgeHandler> = HashMap()
    private val handler: BridgeHandler = HANDLER_DEFAULT
    private var uniqueId: Long = 0

    var javascriptEnabled = false
        set(value) {
            if (field == value) return
            field = value
            if (::settings.isInitialized) settings.javaScriptEnabled = value
        }

    init {
        context.withStyledAttributes(attrs, R.styleable.X5WebViewDefault, defStyleAttr, 0) {
            javascriptEnabled =
                getBoolean(R.styleable.X5WebViewDefault_javascriptEnabled, javascriptEnabled)
        }
        settings = WebUtil.updateSetting(this, javascriptEnabled)
        setVerticalScrollbarOverlay(false)
        setHorizontalScrollbarOverlay(false)
        setBackgroundColor(Color.TRANSPARENT)
        setWebContentsDebuggingEnabled(false)
    }


    override fun onStateChanged(
        source: LifecycleOwner, event: Lifecycle.Event
    ) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {}

            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                //激活WebView为活跃状态，能正常执行网页的响应
                onResume()
            }

            Lifecycle.Event.ON_PAUSE -> {
                //通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
                onPause()
            }

            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {
                messages = null
                callbacks.clear()
                handlers.clear()
                WebUtil.destroy(this)
            }

            Lifecycle.Event.ON_ANY -> {}
        }
    }

    override fun registerHandler(handlerName: String, handler: BridgeHandler) {
        if (!javascriptEnabled) return
        handlers.put(handlerName, handler)
    }

    override fun registerHandler(handlerNames: List<String>, handler: BridgeHandler) {
        if (!javascriptEnabled) return
        handlerNames.forEach { handlers.put(it, handler) }
    }

    override fun send(data: String?) {
        Log.i("javascriptEnabled:$javascriptEnabled data:$data")
        doSend(null, data, null)
    }

    override fun send(data: String?, callback: BridgeCallback?) {
        Log.i("javascriptEnabled:$javascriptEnabled data:$data")
        doSend(null, data, callback)
    }

    override fun callHandler(handlerName: String, data: String?, callBack: BridgeCallback?) {
        Log.i("javascriptEnabled:$javascriptEnabled handlerName:$handlerName data:$data")
        doSend(handlerName, data, callBack)
    }

    override fun handlerReturnData(url: String) {
        Log.i("javascriptEnabled:$javascriptEnabled url:$url")
        if (!javascriptEnabled) return
        val handlerName = BridgeUtil.getFunctionFromReturnUrl(url)
        if (handlerName.isNullOrEmpty().not()) {
            val callback: BridgeCallback = callbacks[handlerName] ?: return
            val data = BridgeUtil.getDataFromReturnUrl(url)
            callback.call(data)
            callbacks.remove(handlerName)
        }
    }

    override fun flushMessageQueue() {
        ContextCompat.getMainExecutor(context).execute {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, object : BridgeCallback {
                override fun call(data: String?) {
                    val list = BridgeMessage.toList(data)
                    if (list.isNullOrEmpty()) return
                    list.forEach { message ->
                        callbacks[message.responseId]?.let {
                            it.call(message.data)
                            callbacks.remove(message.responseId)
                        }
                        val callback = getCallBackFunction(message.callbackId, message.handlerName)
                        val handler = getBridgeHandler(message.handlerName)
                        if (handler != null && message.handlerName.isNullOrEmpty().not()) {
                            handler.handler(message.handlerName, message.data, callback)
                        }
                    }
                }
            })
        }
    }

    override fun getMessage() = messages
    override fun setMessage(list: MutableList<BridgeMessage>?) {
        this.messages = list
    }

    override fun dispatchMessage(message: BridgeMessage) {
        Log.i("javascriptEnabled:$javascriptEnabled message:$message")
        if (!javascriptEnabled) return
        var json: String = message.toJson() ?: return
        //escape special characters for json string  为json字符串转义特殊字符
        json = json.replace("(\\\\)([^utrn])".toRegex(), "\\\\\\\\$1$2")
        json = json.replace("(?<=[^\\\\])(\")".toRegex(), "\\\\\"")
        json = json.replace("(?<=[^\\\\])(\')".toRegex(), "\\\\\'")
        json = json.replace("%7B".toRegex(), URLEncoder.encode("%7B", "UTF-8"))
        json = json.replace("%7D".toRegex(), URLEncoder.encode("%7D", "UTF-8"))
        json = json.replace("%22".toRegex(), URLEncoder.encode("%22", "UTF-8"))
        val javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, json)
        Log.json(json)
        // 必须要找主线程才会将数据传递出去 --- 划重点
        ContextCompat.getMainExecutor(context).execute {
            //开始执行js中_handleMessageFromNative方法
            if (javascriptCommand.length >= URL_MAX_CHARACTER_NUM) {
                this.evaluateJavascript(javascriptCommand, null)
            } else this.loadUrl(javascriptCommand)
        }
    }

    override fun unregisterHandler(handlerName: String) {
        if (handlerName.isNotEmpty()) handlers.remove(handlerName)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
    }

    fun bind(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
    }

    fun client(callback: IX5WebViewCallback) {
        webViewClient = DefaultX5WebViewClient(callback)
        webChromeClient = DefaultX5WebChromeClient(callback)
    }

    fun loadUrl(jsUrl: String, callback: BridgeCallback) {
        Log.i("javascriptEnabled:$javascriptEnabled jsUrl:$jsUrl")
        if (!javascriptEnabled) return
        this.loadUrl(jsUrl)
        callbacks.put(BridgeUtil.parseFunctionName(jsUrl, BridgeUtil.BRIDGE_JS_FILE_NAME), callback)
    }

    private fun doSend(handlerName: String?, data: String?, callback: BridgeCallback?) {
        Log.i("javascriptEnabled:$javascriptEnabled handlerName:$handlerName data:$data")
        val bridgeMessage = BridgeMessage(handlerName = handlerName, data = data)
        if (callback != null) {
            val callbackID = getCallbackID()
            callbacks.put(callbackID, callback)
            bridgeMessage.callbackId = callbackID
        }
        queueMessage(bridgeMessage)
    }

    private fun getCallbackID(): String {
        val time = SystemClock.currentThreadTimeMillis()
        val value = "${++uniqueId}${BridgeUtil.UNDERLINE_STR}${time}"
        return String.format(BridgeUtil.CALLBACK_ID_FORMAT, value)
    }

    private fun getCallBackFunction(callbackId: String, handlerName: String?): BridgeCallback {
        return object : BridgeCallback {
            override fun call(data: String?) {
                if (callbackId.isNotEmpty()) {
                    val message = BridgeMessage(
                        handlerName = handlerName,
                        callbackId = callbackId,
                        data = data,
                    )
                    queueMessage(message)
                }
            }
        }
    }

    private fun getBridgeHandler(handlerName: String?): BridgeHandler? {
        return if (handlerName.isNullOrEmpty().not()) handlers[handlerName]
        else handler
    }

    /*list<message> != null 添加到消息集合否则分发消息*/
    private fun queueMessage(message: BridgeMessage) {
        if (messages.isNullOrEmpty().not()) messages!!.add(message) else dispatchMessage(message)
    }

}