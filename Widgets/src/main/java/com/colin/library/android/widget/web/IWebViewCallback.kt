package com.colin.library.android.widget.web

import android.net.Uri
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.PermissionRequest
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient.FileChooserParams
import com.tencent.smtt.sdk.WebView

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-08 12:39
 *
 * Des   :WebViewCallback
 */
interface IWebViewCallback {
    fun start(url: String?) {}

    fun progress(progress: Int) {}

    fun finished(url: String?) {}

    fun error(url: String, error: String) {}

    fun title(title: String?) {}

    fun intercept(view: WebView, url: String?) = false

    fun dialog(url: String?, message: String?, value: String?, result: JsResult?) = false

    fun permissionRequest(request: PermissionRequest) = false

    fun permissionCancel(request: PermissionRequest) = false

    fun openFile(callback: ValueCallback<Array<out Uri>>, params: FileChooserParams?) = false
}