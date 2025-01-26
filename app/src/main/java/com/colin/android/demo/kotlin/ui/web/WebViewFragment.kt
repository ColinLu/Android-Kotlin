package com.colin.android.demo.kotlin.ui.web

import android.os.Bundle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentWebViewBinding

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

    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        viewBinding.mWebView.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.mWebView.onPause()
    }

    key
}