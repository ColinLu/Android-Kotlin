package com.colin.android.demo.kotlin.ui.view

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.toNavigate
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.launch


class ViewFragment : AppFragment<LayoutRefreshListBinding, ViewViewMode>() {
    private val adapter by lazy {
        StringAdapter().apply {
            empty = R.layout.layout_empty
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.clear()
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            list.apply {
                this.adapter = this@ViewFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, _ ->
                when (item) {
                    getString(R.string.title_web) -> {
                        toNavigate(this@ViewFragment, R.id.action_webIndex)
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.list.flowWithLifecycle(lifecycle).collect {
                    Log.i("list$it")
                    it.apply { adapter.submitList(it) }
                    viewBinding.refresh.isRefreshing = false
                }
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData()
    }


}