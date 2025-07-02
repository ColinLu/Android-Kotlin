package com.colin.library.android.web

import android.net.Uri
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-08 12:39
 *
 * Des   :WebViewCallback
 */
interface IWebViewCallback {
    fun intercept(url: String?) = false

    fun start(url: String?) {}

    fun progress(progress: Int) {}

    fun finished(url: String?) {}

    fun error(url: String, error: String) {}

    fun title(title: String?) {}

    fun dialog(url: String?, message: String?, value: String?, result: JsResult?) = false

    fun permissionRequest(request: PermissionRequest) = false

    fun permissionCancel(request: PermissionRequest) = false

    fun openFile(callback: ValueCallback<Array<out Uri>>, params: FileChooserParams?) = false
}