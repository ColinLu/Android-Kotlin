package com.colin.android.demo.kotlin.ui.web

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentWebViewBinding
import com.colin.android.demo.kotlin.ui.MainActivity
import com.colin.library.android.utils.Log
import com.colin.library.android.web.IWebViewCallback
import com.colin.library.android.web.IX5WebViewCallback
import com.colin.library.android.web.WebViewDefault
import com.colin.library.android.web.X5WebViewDefault
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:32
 *
 * Des   :WebViewFragment
 */
class WebViewFragment : AppFragment<FragmentWebViewBinding, WebViewModel>() {
    companion object {
        const val EXTRAS_URL: String = "url"
        const val EXTRAS_X5: String = "x5"

        @JvmStatic
        fun newInstance(url: String, x5: Boolean = false): WebViewFragment {
            val args = Bundle().apply {
                putString(EXTRAS_URL, url)
                putBoolean(EXTRAS_X5, x5)
            }
            val fragment = WebViewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var firstLoadFinish = false

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = goBack()
        }
    }
    private val webCallback = object : IWebViewCallback {
        override fun intercept(request: android.webkit.WebResourceRequest): Boolean {
            Log.i("intercept request:$request")
            return loadUrl(request.url)
        }

        override fun start(url: String?) {
            viewModel.viewModelScope.launch { viewModel.loading(true) }
            viewBinding.progress.isVisible = true
        }

        override fun progress(progress: Int) {
            viewBinding.progress.progress = progress
        }

        override fun finished(url: String?) {
            firstLoadFinish = true
            viewBinding.progress.isVisible = false
            viewModel.viewModelScope.launch { viewModel.loading(false) }
        }

        override fun title(title: String?) {
            val context = requireContext()
            if (context is MainActivity) context.update(title)
        }

        override fun dialog(
            url: String?, message: String?, value: String?, result: android.webkit.JsResult?
        ): Boolean {
            Log.i("url:$url message:$message value:$value")
            return super.dialog(url, message, value, result)
        }

        override fun permissionRequest(request: android.webkit.PermissionRequest): Boolean {
            Log.i("request:$request")
            return super.permissionRequest(request)
        }

        override fun permissionCancel(request: android.webkit.PermissionRequest): Boolean {
            Log.i("request:$request")
            return super.permissionCancel(request)
        }
    }
    private val x5webCallback = object : IX5WebViewCallback {
        override fun intercept(request: com.tencent.smtt.export.external.interfaces.WebResourceRequest): Boolean {
            Log.i("intercept request:$request")
            return loadUrl(request.url)
        }

        override fun start(url: String?) {
            viewModel.viewModelScope.launch { viewModel.loading(true) }
            viewBinding.progress.isVisible = true
        }

        override fun progress(progress: Int) {
            viewBinding.progress.progress = progress
        }

        override fun finished(url: String?) {
            firstLoadFinish = true
            viewBinding.progress.isVisible = false
            viewModel.viewModelScope.launch { viewModel.loading(false) }
        }

        override fun title(title: String?) {
            val context = requireContext()
            if (context is MainActivity) context.update(title)
        }

        override fun dialog(
            url: String?,
            message: String?,
            value: String?,
            result: com.tencent.smtt.export.external.interfaces.JsResult?
        ): Boolean {
            Log.i("url:$url message:$message value:$value")
            return super.dialog(url, message, value, result)
        }

        override fun permissionRequest(request: com.tencent.smtt.export.external.interfaces.PermissionRequest): Boolean {
            Log.i("request:$request")
            return super.permissionRequest(request)
        }

        override fun permissionCancel(request: com.tencent.smtt.export.external.interfaces.PermissionRequest): Boolean {
            Log.i("request:$request")
            return super.permissionCancel(request)
        }
    }

    override fun goBack(): Boolean {
        val view =
            viewBinding.linear.findViewById<View?>(com.colin.library.android.web.R.id.web_view)
        if (view is com.tencent.smtt.sdk.WebView && view.canGoBack()) {
            view.goBack()
            return true
        }
        if (view is android.webkit.WebView && view.canGoBack()) {
            view.goBack()
            return true
        }
        return super.goBack()
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val isX5 = bundle?.getBoolean(EXTRAS_X5) == true
        viewBinding.linear.addView(
            createWebView(requireContext(), isX5), 1, LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1F
            )
        )
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }


    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        val url = bundle?.getString(EXTRAS_URL) ?: getString(R.string.query_web_hint_link)
        loadUrl(url.toUri())
    }

    override fun loadData(refresh: Boolean) {
        if (firstLoadFinish) {
            val view = getWebView()
            if (view is com.tencent.smtt.sdk.WebView) view.reload()
            else if (view is android.webkit.WebView) view.reload()
        }
    }

    private fun createWebView(context: Context = requireContext(), isX5: Boolean = false): View {
        if (isX5) {
            return X5WebViewDefault(context = context).also {
                it.id = com.colin.library.android.web.R.id.web_view
                it.javascriptEnabled = true
                it.client(x5webCallback)
                it.bind(lifecycle)
            }
        } else {
            return WebViewDefault(context = context).also {
                it.id = com.colin.library.android.web.R.id.web_view
                it.javascriptEnabled = true
                it.client(webCallback)
                it.bind(lifecycle)
            }
        }
    }

    private fun loadUrl(url: Uri): Boolean {
        Log.i("loadUrl:$url")
        val view = getWebView()
        if (view is android.webkit.WebView) view.loadUrl(url.toString())
        else if (view is com.tencent.smtt.sdk.WebView) view.loadUrl(url.toString())
        return true
    }

    private fun getWebView(): View? {
        return viewBinding.linear.findViewById<View>(com.colin.library.android.web.R.id.web_view)
    }

}


