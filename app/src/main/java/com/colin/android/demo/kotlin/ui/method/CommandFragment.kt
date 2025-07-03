package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.dialog.DialogTips
import com.colin.android.demo.kotlin.ui.MainActivity
import com.colin.library.android.utils.CommandUtil
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:19
 *
 * Des   :CommandFragment
 */
class CommandFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy {
        StringAdapter().apply {
            empty = R.layout.layout_empty
        }
    }

    override fun bindViewModelStore(): ViewModelStore {
        return requireActivity().viewModelStore
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            list.apply {
                setHasFixedSize(true)
                this.adapter = this@CommandFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { view, item, position ->
                command(item)
            }

            initSearch((requireActivity() as? MainActivity)?.getSearchView())

        }

    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.list.flowWithLifecycle(lifecycle).collect {
                    Log.i("LogFragment", it)
                    it.apply { adapter.submitList(it) }
                    viewBinding.refresh.isRefreshing = false
                }
            }
        }
    }


    override fun loadData(refresh: Boolean) {
        viewModel.loadData(R.array.command_list)
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateSearch(true)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as? MainActivity)?.getSearchView()?.onActionViewCollapsed()
        viewModel.updateSearch(false)
    }

    /**
     * 初始化搜索框
     *
     */
    private fun initSearch(search: SearchView?) {
        search?.apply {
            queryHint = getString(R.string.query_web_hint_link)
            isSubmitButtonEnabled = true
            setOnQueryTextListener(searchListener)
        }
    }

    private val searchListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(key: String): Boolean {
            command(key, true)
            (requireActivity() as? MainActivity)?.getSearchView()?.onActionViewCollapsed()
            return true
        }

        override fun onQueryTextChange(key: String): Boolean {
            Log.i(key)
            return true
        }
    }

    private fun command(command: String, root: Boolean = false) {
        val result = CommandUtil.execCmd(root, command)
        ContextCompat.getMainExecutor(requireContext()).execute {
            DialogTips.newInstance(command, "success:${result.success}\nfailure${result.failure}")
                .show(this)
        }
    }
}