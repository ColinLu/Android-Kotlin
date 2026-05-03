package com.colin.library.android.utils.ext

import android.os.SystemClock
import android.view.View
import com.colin.library.android.utils.TIMEOUT_CLICK

private var lastClickTime = 0L

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-01 02:52
 *
 * Des   :View扩展函数，提供防抖点击和可见性控制
 */


/**
 * 为多个View设置防重复点击事件
 *
 * @param views 需要设置点击事件的View数组
 * @param interval 时间间隔（毫秒），默认500毫秒
 * @param click 点击回调
 */
fun onClick(vararg views: View, interval: Long = TIMEOUT_CLICK, click: (View) -> Unit) {
    views.forEach {
        it.onClick(interval = interval) { view -> click.invoke(view) }
    }
}

/**
 * 为单个View设置防重复点击事件
 *
 * @param interval 时间间隔（毫秒），默认500毫秒
 * @param click 点击回调
 */
fun View.onClick(interval: Long = TIMEOUT_CLICK, click: (view: View) -> Unit) {
    setOnClickListener {
        val current = SystemClock.elapsedRealtime()
        if (lastClickTime != 0L && (current - lastClickTime < interval)) {
            return@setOnClickListener
        }
        lastClickTime = current
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
 * 判断View是否可见
 *
 * @return true表示可见，false表示不可见
 */
fun View.isVisible() = this.visibility == View.VISIBLE
