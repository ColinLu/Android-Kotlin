package com.colin.android.demo.kotlin.ui.widget.web

import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.toNavigate
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
    private lateinit var stringAdapter: StringAdapter
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.refresh.apply {
            setColorSchemeResources(R.color.colorAccent)
        }
        if (::stringAdapter.isInitialized.not()) stringAdapter = StringAdapter()
        viewBinding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = stringAdapter
            itemAnimator = DefaultItemAnimator()
        }

        viewBinding.refresh.setOnRefreshListener {
            viewModel.loadData(true)
        }
        stringAdapter.onItemClickListener = { view, item, position ->
            toWebView(item, false)
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewModel.history.observe {
            Log.i("history:$it")
            stringAdapter.submitList(it)
            viewBinding.refresh.isRefreshing = false
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData(refresh)
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
            toWebView(key, true)
            return true
        }

        override fun onQueryTextChange(key: String): Boolean {
            Log.i(key)
            return true
        }
    }

    private fun toWebView(url: String?, record: Boolean) {
        if (url.isNullOrEmpty()) {
            return
        }
        if (record) viewModel.record(url)
        val bundle = Bundle()
        bundle.putString(WebViewFragment.EXTRAS_URL, url)
        toNavigate(this, R.id.action_web, bundle)
    }
}