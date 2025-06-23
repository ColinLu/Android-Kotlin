package com.colin.library.android.widget.web

import android.content.Context
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import androidx.core.content.withStyledAttributes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.R
import com.colin.library.android.widget.web.client.DefaultWebChromeClient
import com.colin.library.android.widget.web.client.DefaultWebViewClient
import com.google.gson.GsonBuilder
import com.google.gson.Strictness

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-08 12:46
 *
 * Des   :CustomWebView
 */
class CustomWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : com.tencent.smtt.sdk.WebView(context, attrs, defStyleAttr), LifecycleEventObserver {
    private val gson by lazy {
        GsonBuilder().setStrictness(Strictness.LENIENT).create()
    }
    private var useJavascript = false

    init {
        context.withStyledAttributes(attrs, R.styleable.CustomWebView, defStyleAttr, 0) {
            useJavascript = getBoolean(R.styleable.CustomWebView_useJavascript, useJavascript)
        }
        if (useJavascript) WebServiceConnection.instance.bindAIDL(context)
    }

    fun bind(lifecycle: Lifecycle, callback: IWebViewCallback) {
        lifecycle.addObserver(this)
        webViewClient = DefaultWebViewClient(callback)
        webChromeClient = DefaultWebChromeClient(callback)
    }

    override fun onStateChanged(
        source: LifecycleOwner, event: Lifecycle.Event
    ) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {}

            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {
                onResume()
            }

            Lifecycle.Event.ON_PAUSE -> {
                onPause()
            }

            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {
                updateUseJavascript(false)
                clearCache(true)
                clearHistory()
                clearFormData()
                destroy()
            }

            Lifecycle.Event.ON_ANY -> {}
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

    }

    fun updateUseJavascript(use: Boolean) {
        if (useJavascript == use) return
        useJavascript = use
        if (use) WebServiceConnection.instance.bindAIDL(context)
        else WebServiceConnection.instance.unbindAIDL(context)
    }

    @JavascriptInterface
    fun takeNativeAction(json: String?) {
        Log.w("useJavascript:$useJavascript")
        Log.json(json)
        val param = gson.fromJson(json, JsParam::class.java)
        if (useJavascript && param.name.isNotEmpty()) {
            WebServiceConnection.instance.executeCommand(
                param.name, gson.toJson(param.json), this
            )
        }
    }


    fun handleCallback(callbackname: String, response: String?) {
        Log.w("useJavascript:$useJavascript callbackname:$callbackname response:$response")
        if (useJavascript && callbackname.isNotEmpty() && response.isNullOrEmpty().not()) {
            post {
                val jscode = "javascript:myjs.callback('$callbackname',$response)"
                evaluateJavascript(jscode, null)
            }
        }
    }

    interface OnScrollChangedListener {
        fun onPageEnd(l: Int, t: Int, oldL: Int, oldT: Int)

        fun onPageTop(l: Int, t: Int, oldL: Int, oldT: Int)

        fun onScrollChanged(l: Int, t: Int, oldL: Int, oldT: Int)

        fun onTouchScreen()
    }


}