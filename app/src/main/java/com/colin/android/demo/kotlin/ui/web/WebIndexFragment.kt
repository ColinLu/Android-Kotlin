package com.colin.android.demo.kotlin.ui.web

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.DefaultItemAnimator
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
    private lateinit var adapter: StringAdapter

    override fun bindViewModelStore(): ViewModelStore {
        return requireActivity().viewModelStore
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        if (::adapter.isInitialized.not()) adapter = StringAdapter()
        viewBinding.list.apply {
            setHasFixedSize(true)
            adapter = this@WebIndexFragment.adapter
            itemAnimator = DefaultItemAnimator()
        }

        adapter.onItemClickListener = { _, item, _ ->
            toWebView(item, false)
        }
        val search = (requireActivity() as? MainActivity)?.getMenu()
            ?.findItem(R.id.action_search) as? SearchView
        Log.e("${requireActivity().javaClass.simpleName}")
        Log.e("${(requireActivity() as? MainActivity)?.getMenu()}")
        Log.e("$search")
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewModel.history.observe { adapter.submitList(it) }
        val search = (requireActivity() as? MainActivity)?.getMenu()
            ?.findItem(R.id.action_search) as? SearchView
        Log.e("${requireActivity().javaClass.simpleName}")
        Log.e("${(requireActivity() as? MainActivity)?.getMenu()}")
        Log.e("$search")
    }


    override fun loadData(refresh: Boolean) {
        viewModel.loadData(refresh)
        val search = (requireActivity() as? MainActivity)?.getMenu()
            ?.findItem(R.id.action_search) as? SearchView
        Log.e("${requireActivity().javaClass.simpleName}")
        Log.e("${(requireActivity() as? MainActivity)?.getMenu()}")
        Log.e("$search")
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateSearch(true)
        val search = (requireActivity() as? MainActivity)?.getMenu()
            ?.findItem(R.id.action_search) as? SearchView
        Log.e("${requireActivity().javaClass.simpleName}")
        Log.e("${(requireActivity() as? MainActivity)?.getMenu()}")
        Log.e("$search")
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as? MainActivity)?.getSearchView()?.onActionViewCollapsed()
        viewModel.updateSearch(false)
    }

    /**
     * 初始化搜索框
     *
     * @param searchItem
     */
    private fun initSearch(search: SearchView?) {
        Log.e("search:$search")
        search?.apply {
            queryHint = getString(R.string.query_web_hint_link)
            isSubmitButtonEnabled = true
            setOnQueryTextListener(searchListener)
        }
    }

    private val searchListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(key: String): Boolean {
            toWebView(key, true)
            (requireActivity() as? MainActivity)?.getSearchView()?.onActionViewCollapsed()
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