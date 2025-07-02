package com.colin.library.android.widget.scroll

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.webkit.WebView
import androidx.core.os.BundleCompat
import com.colin.library.android.utils.ext.dp
import com.colin.library.android.widget.scroll.INestedScroll.OnScrollNotify
import kotlin.math.max
import kotlin.math.min

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-24 10:07
 *
 * Des   :NestedScrollTopWebView
 */
class NestedScrollTopWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs, 0), INestedScrollTop {
    private var mScrollNotify: OnScrollNotify? = null

    init {
        isVerticalScrollBarEnabled = false
    }


    override fun consumeScroll(dyUnconsumed: Int): Int {
        // compute the consumed value
        var scrollY = getScrollY()
        val maxScrollY = getScrollOffsetRange()
        // the scrollY may be negative or larger than scrolling range
        scrollY = max(0.0, min(scrollY.toDouble(), maxScrollY.toDouble())).toInt()
        var dy = 0
        if (dyUnconsumed < 0) dy = max(dyUnconsumed.toDouble(), -scrollY.toDouble()).toInt()
        else if (dyUnconsumed > 0) dy =
            min(dyUnconsumed.toDouble(), (maxScrollY - scrollY).toDouble()).toInt()
        scrollBy(0, dy)
        return dyUnconsumed - dy
    }

    override fun getCurrentScroll(): Int {
        val scrollY = getScrollY()
        val scrollRange = getScrollOffsetRange()
        return max(0.0, min(scrollY.toDouble(), scrollRange.toDouble())).toInt()
    }

    override fun getScrollOffsetRange(): Int {
        return computeVerticalScrollRange() - height
    }

    override fun injectScrollNotifier(notify: OnScrollNotify?) {
        mScrollNotify = notify
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollNotify?.notify(getCurrentScroll(), getScrollOffsetRange())
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(INSTANCE_SCROLL_OFFSET, scrollY)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state
            val scrollY: Int = bundle.getInt(INSTANCE_SCROLL_OFFSET, 0).dp()
            exec("javascript:scrollTo(0, $scrollY)")
            super.onRestoreInstanceState(
                BundleCompat.getParcelable<Parcelable>(
                    bundle, INSTANCE_STATE, Parcelable::class.java
                )
            )
        } else super.onRestoreInstanceState(state)
    }

    private fun exec(jsCode: String) {
        evaluateJavascript(jsCode, null)
    }

    companion object {
        const val INSTANCE_STATE: String = "INSTANCE_STATE"
        const val INSTANCE_SCROLL_OFFSET: String = "INSTANCE_SCROLL_OFFSET"
    }
}
