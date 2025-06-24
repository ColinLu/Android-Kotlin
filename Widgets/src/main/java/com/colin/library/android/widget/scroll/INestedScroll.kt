package com.colin.library.android.widget.scroll

import android.view.View
import androidx.annotation.Px
import androidx.viewpager2.widget.ViewPager2.ScrollState

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-24 10:03
 *
 * Des   :INestedScroll
 */
interface INestedScroll {
    fun injectScrollNotifier(notify: OnScrollNotify?)

    interface OnScrollNotify {
        fun notify(@Px offset: Int, @Px range: Int) {
        }

        fun onScrollStateChanged(view: View, @ScrollState scrollState: Int) {
        }
    }
}