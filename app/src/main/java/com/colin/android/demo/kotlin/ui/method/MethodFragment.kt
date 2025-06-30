package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.toNavigate
import com.colin.android.demo.kotlin.ui.list.ListFragment
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.launch


class MethodFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy {
        StringAdapter().apply {
            empty = R.layout.layout_empty
        }
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            list.apply {
                this.adapter = this@MethodFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, _ ->
                when (item) {
                    getString(R.string.title_log) -> {
                        toNavigate(this@MethodFragment, R.id.action_to_log)
                    }

                    getString(R.string.method_path) -> {
                        ListFragment.toNavigate(this@MethodFragment, R.array.path_list)
                    }

                    getString(R.string.title_web) -> {
                        toNavigate(this@MethodFragment, R.id.action_webIndex)
                    }

                    "Crash" -> {
                        throw RuntimeException("test crash")
                    }

                    else -> {
                        Log.i(item)
                    }
                }
            }
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.list.flowWithLifecycle(lifecycle).collect {
                Log.i(it)
                adapter.submitList(it)
                viewBinding.refresh.isRefreshing = false
            }
        }

    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData()
    }


}