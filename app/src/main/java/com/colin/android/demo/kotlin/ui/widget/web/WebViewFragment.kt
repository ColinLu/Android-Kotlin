package com.colin.android.demo.kotlin.ui.widget.web

import android.os.Bundle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentWebViewBinding
import com.colin.library.android.widget.web.IWebViewCallback
import com.colin.library.android.widget.web.client.DefaultWebSetting

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

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        val url = bundle?.getString(EXTRAS_URL) ?: getString(R.string.query_web_hint_link)
        DefaultWebSetting.updateSetting(viewBinding.webView)
        viewBinding.webView.bind(lifecycle, object : IWebViewCallback {

        })
        viewBinding.webView.loadUrl(url)
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

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