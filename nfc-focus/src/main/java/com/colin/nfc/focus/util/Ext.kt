package com.colin.nfc.focus.util

import android.util.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2026-07-02 23:04
 *
 * Des   :Ext
 */
private const val GLOBAL_TAG = "NFC_SDK"
internal fun String.i(tag: String = "") {
    Log.i("${GLOBAL_TAG}_${tag}", this)
}

internal fun String.w(tag: String = "") {
    Log.w("${GLOBAL_TAG}_${tag}", this)
}

internal fun String.e(tag: String = "", tr: Throwable? = null) {
    Log.e("${GLOBAL_TAG}_${tag}", this, tr)
}