package com.colin.library.android.widget.scroll

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-24 10:03
 *
 * Des   :INestedScrollTop
 */
interface INestedScrollTop : INestedScroll {
    /**
     * consume scroll
     *
     * @param dyUnconsumed the delta value to consume
     * @return the remain unconsumed value
     */
    fun consumeScroll(dyUnconsumed: Int): Int

    fun getCurrentScroll(): Int

    fun getScrollOffsetRange(): Int
}