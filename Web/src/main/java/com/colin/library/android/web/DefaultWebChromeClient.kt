package com.colin.library.android.web

import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.colin.library.android.utils.Log

class DefaultWebChromeClient(private val callBack: IWebViewCallback) : WebChromeClient() {

    override fun onReceivedTitle(view: WebView, title: String?) {
        callBack.title(title)
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        callBack.progress(newProgress)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        Log.d("onConsoleMessage: ${consoleMessage?.message()}")
        return super.onConsoleMessage(consoleMessage)
    }


    override fun onJsAlert(
        view: WebView, url: String?, message: String?, result: JsResult?
    ): Boolean {
        return if (callBack.dialog(url, message, null, result)) true
        else super.onJsAlert(view, url, message, result)
    }

    override fun onJsConfirm(
        view: WebView, url: String?, message: String?, result: JsResult?
    ): Boolean {
        return if (callBack.dialog(url, message, null, result)) true
        else super.onJsConfirm(view, url, message, result)
    }

    override fun onJsBeforeUnload(
        view: WebView, url: String?, message: String?, result: JsResult?
    ): Boolean {
        return if (callBack.dialog(url, message, null, result)) true
        else super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onJsPrompt(
        view: WebView, url: String?, message: String?, value: String?, result: JsPromptResult?
    ): Boolean {
        return if (callBack.dialog(url, message, value, result)) true
        else super.onJsPrompt(view, url, message, value, result)
    }

    override fun onPermissionRequest(request: PermissionRequest) {
        if (!callBack.permissionRequest(request)) super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest) {
        if (!callBack.permissionCancel(request)) super.onPermissionRequestCanceled(request)
    }

    override fun onShowFileChooser(
        view: WebView, uri: ValueCallback<Array<out Uri>>, params: FileChooserParams?
    ): Boolean {
        return if (callBack.openFile(uri, params)) true
        else super.onShowFileChooser(view, uri, params)
    }

}

