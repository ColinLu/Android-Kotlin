package com.colin.library.android.utils

import android.view.View

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-24
 *
 * Des   :Kotlin常用扩展方法
 */


private var lastClickTime = 0L
fun View.onClick(time: Int = 500, block: (View) -> Unit) {
    setOnClickListener {
        val nowTime = System.currentTimeMillis()
        if (nowTime - lastClickTime > time) {
            lastClickTime = nowTime
            block.invoke(it)
        }
    }
}

fun Array<View>.onClick(block: (View) -> Unit) {
    forEach { it ->
        it.onClick { block.invoke(it) }
    }
}