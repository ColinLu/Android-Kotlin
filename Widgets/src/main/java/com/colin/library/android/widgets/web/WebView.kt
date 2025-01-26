package com.colin.library.android.widgets.web

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.colin.library.android.widgets.R

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:00
 *
 * Des   :WebView
 */
class WebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), LifecycleEventObserver {
    private var url : String?= null
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.WebView, defStyleAttr, 0)
    }

    fun bindLifecycle(lifecycle: Lifecycle?) {
        lifecycle?.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_PAUSE -> onPause()
            else -> {}
        }
    }


}