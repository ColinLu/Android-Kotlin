package com.colin.library.android.utils

import android.view.View

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-18
 *
 * Des   :View 扩展
 */
private var lastClickTime = 0L

fun Array<View>.onClick(block: (View) -> Unit) {
    forEach { it -> it.onClick { block.invoke(it) } }
}

/*按钮防抖机制，默认500ms*/
fun View.onClick(time: Int = Constants.VIEW_CLICK_DEF, block: (View) -> Unit) {
    setOnClickListener {
        L.e("setOnClickListener")
        val nowTime = System.currentTimeMillis()
        if (nowTime - lastClickTime > time) {
            lastClickTime = nowTime
            block.invoke(it)
        }
    }
}