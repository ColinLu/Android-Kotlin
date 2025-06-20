package com.colin.library.android.utils.ext

import android.R.attr.action
import android.view.View
import com.colin.library.android.utils.INVALID
import com.colin.library.android.utils.TIMEOUT_CLICK

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-01 02:52
 *
 * Des   :ViewExt
 */
/**
 * 设置防止重复点击事件
 * @param views 需要设置点击事件的view
 * @param interval 时间间隔 默认0.5秒
 * @param onClick 点击触发的方法
 */
fun onClick(vararg views: View, interval: Long = TIMEOUT_CLICK, click: (View) -> Unit) {
    views.forEach {
        it.onClick(interval = interval) { view -> click.invoke(view) }
    }
}

/**
 * 防止重复点击事件 默认0.5秒内不可重复点击
 * @param interval 时间间隔 默认0.5秒
 * @param action 执行方法
 */

private var lastClickTime = INVALID.toLong()

fun View.onClick(interval: Long = TIMEOUT_CLICK, click: (view: View) -> Unit) {
    setOnClickListener {
        val current = System.currentTimeMillis()
        if (lastClickTime != 0L && (current - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = current
        click.invoke(it)
    }
}

