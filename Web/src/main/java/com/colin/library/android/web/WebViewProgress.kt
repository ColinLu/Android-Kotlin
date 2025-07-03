package com.colin.library.android.web

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.withStyledAttributes

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-03 10:27
 *
 * Des   :WebViewProgress
 *
 */
class WebViewProgress @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private lateinit var web: View
    private lateinit var progress: ProgressBar
    private var isX5: Boolean = false
    private var javascriptEnabled: Boolean = false

    init {
        context.withStyledAttributes(attrs, R.styleable.WebViewProgress, defStyleAttr, 0) {
            isX5 = getBoolean(R.styleable.WebViewProgress_isX5, isX5)
            javascriptEnabled = getBoolean(R.styleable.WebViewProgress_javascriptEnabled, javascriptEnabled)
        }

    }
}