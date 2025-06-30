package com.colin.android.demo.kotlin.ui.web

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentWebViewBinding
import com.colin.android.demo.kotlin.ui.MainActivity
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.web.IWebViewCallback
import com.colin.library.android.widget.web.client.DefaultWebSetting
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.PermissionRequest
import com.tencent.smtt.sdk.WebView
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

        @JvmStatic
        fun newInstance(url: String): WebViewFragment {
            val args = Bundle().apply {
                putString(EXTRAS_URL, url)
            }
            val fragment = WebViewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun goBack(): Boolean {
        if (viewBinding.webView.canGoBack()) {
            viewBinding.webView.goBack()
            return true
        }
        return super.goBack()
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val url = bundle?.getString(EXTRAS_URL) ?: getString(R.string.query_web_hint_link)
        DefaultWebSetting.updateSetting(viewBinding.webView)
        viewBinding.webView.bind(lifecycle, object : IWebViewCallback {
            override fun intercept(view: WebView, url: String?): Boolean {
                Log.i("intercept url:$url")
                return false
            }

            override fun start(url: String?) {
                viewModel.viewModelScope.launch { viewModel.loading(true) }
                viewBinding.progress.isVisible = true
            }

            override fun progress(progress: Int) {
                viewBinding.progress.progress = progress
            }

            override fun finished(url: String?) {
                viewBinding.progress.isVisible = false
                viewModel.viewModelScope.launch { viewModel.loading(false) }
            }

            override fun title(title: String?) {
                val context = requireContext()
                if (context is MainActivity) context.update(title)
            }

            override fun dialog(
                url: String?, message: String?, value: String?, result: JsResult?
            ): Boolean {
                Log.i("url:$url message:$message value:$value")
                return super.dialog(url, message, value, result)
            }

            override fun permissionRequest(request: PermissionRequest): Boolean {
                Log.i("request:$request")
                return super.permissionRequest(request)
            }

            override fun permissionCancel(request: PermissionRequest): Boolean {
                Log.i("request:$request")
                return super.permissionCancel(request)
            }
        })
        viewBinding.webView.loadUrl(url)
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }

    override fun loadData(refresh: Boolean) {
        viewBinding.webView.reload()
    }

    override fun onResume() {
        super.onResume()
        viewBinding.webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.webView.onPause()
    }
}


