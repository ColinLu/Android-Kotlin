package com.colin.library.android.utils.ext

import android.os.SystemClock
import android.view.View
import com.colin.library.android.utils.TIMEOUT_CLICK

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-01 02:52
 *
 * Des   :View扩展函数，提供防抖点击和可见性控制
 */

private const val TAG_LAST_CLICK_TIME = -101

/**
 * 为多个View设置防重复点击事件
 */
fun onClick(vararg views: View, interval: Long = TIMEOUT_CLICK, click: (View) -> Unit) {
    views.forEach { it.onClick(interval, click) }
}

/**
 * 为单个View设置防重复点击事件
 */
fun View.onClick(interval: Long = TIMEOUT_CLICK, click: (view: View) -> Unit) {
    setOnClickListener {
        val current = SystemClock.elapsedRealtime()
        val lastClickTime = getTag(TAG_LAST_CLICK_TIME) as? Long ?: 0L
        if (current - lastClickTime < interval) return@setOnClickListener
        setTag(TAG_LAST_CLICK_TIME, current)
        click.invoke(it)
    }
}

/**
 * 设置View的显示/隐藏状态
 *
 * @param visible true显示，false隐藏（GONE）
 */
fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * 设置View的可见性（支持 INVISIBLE）
 */
fun View.invisible(invisible: Boolean) {
    visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}
