package com.colin.android.demo.kotlin.ui.web

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.ui.MainActivity
import com.colin.library.android.utils.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:19
 *
 * Des   :WebIndexFragment
 */
class WebIndexFragment : AppFragment<LayoutRefreshListBinding, WebViewModel>() {
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setMenuVisible(R.id.action_search, true)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setMenuVisible(R.id.action_search, false)
    }

    /**
     * 初始化搜索框
     *
     * @param searchItem
     */
    private fun initSearch(searchItem: MenuItem?) {
        val searchView = searchItem?.actionView as? SearchView ?: return
        searchView.apply {
            queryHint = getString(R.string.query_web_hint_link)
            isSubmitButtonEnabled = true
            setOnQueryTextListener(searchListener)
        }
    }

    private val searchListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(key: String): Boolean {
            Log.i(key)

            return true
        }

        override fun onQueryTextChange(key: String): Boolean {
            Log.i(key)
            return true
        }
    }
}