package com.colin.library.android.network.data

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-12 00:06
 *
 * Des   :ApiException
 */
open class ApiException : Exception {
    var code: Int
    var msg: String

    constructor(code: Int, msg: String) : super(msg) {
        this.code = code
        this.msg = msg
    }

    constructor(code: Int, msg: String, e: Throwable? = null) : super(e) {
        this.code = code
        this.msg = msg
    }
}