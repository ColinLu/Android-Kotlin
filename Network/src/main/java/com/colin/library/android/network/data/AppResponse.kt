package com.colin.library.android.network.data

import com.colin.library.android.utils.INVALID
import com.colin.library.android.utils.ZERO


/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-03 15:38
 *
 * Des   :AppResponse
 */

open class AppResponse<T>(
    private val code: Int = INVALID,
    private val msg: String = "",
    private val data: T? = null
) {
    open fun isSuccess() = getCode() == ZERO

    fun getCode() = code

    fun getMsg() = msg

    fun getData() = data

}

