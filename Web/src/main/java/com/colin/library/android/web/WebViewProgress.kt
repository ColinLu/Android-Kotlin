package com.colin.library.android.web

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.withStyledAttributes
import com.tencent.smtt.sdk.WebView

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-03 10:27
 *
 * Des   :WebViewProgress
 *
 */
@Deprecated("不够方便，暂不推荐")
class WebViewProgress @JvmOverloads constructor(
    private val context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    lateinit var web: View
    lateinit var progress: ProgressBar
    private var isX5: Boolean = false
    private var javascriptEnabled: Boolean = false
    private val WEB_INDEX = 0
    private val PROGRESS_INDEX = 1

    init {
        context.withStyledAttributes(attrs, R.styleable.WebViewProgress, defStyleAttr, 0) {
            isX5 = getBoolean(R.styleable.WebViewProgress_isX5, isX5)
            javascriptEnabled =
                getBoolean(R.styleable.WebViewProgress_javascriptEnabled, javascriptEnabled)
        }
        web = findViewById(R.id.web_view) ?: createWebView(isX5)
        progress = findViewById(R.id.web_progress) ?: createProgress()
    }

    fun destroy() {
        if (web is WebView) WebUtil.destroy(web as WebView)
        if (web is android.webkit.WebView) WebUtil.destroy(web as android.webkit.WebView)
    }

    private fun createWebView(isX5: Boolean): View {
        val view = if (isX5) WebView(context, attrs).apply { id = R.id.web_view }
        else android.webkit.WebView(context, attrs).apply { id = R.id.web_view }
        addView(view, WEB_INDEX, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        return view
    }

    private fun createProgress(): ProgressBar {
        return ProgressBar(context, attrs).apply {
            id = R.id.web_progress
        }.also {
            addView(
                it,
                PROGRESS_INDEX,
                LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply { gravity = Gravity.CENTER })
        }
    }
}