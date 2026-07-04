package com.colin.android.demo.kotlin.ui.widget

import android.os.Bundle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.toNavigate
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.launch


class WidgetFragment : AppFragment<LayoutRefreshListBinding, WidgetViewModel>() {
    private val adapter by lazy { StringAdapter() }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@WidgetFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, _ ->
                when (item) {
                    getString(R.string.widget_web) -> toNavigate(
                        this@WidgetFragment,
                        R.id.action_webIndex
                    )

                    getString(R.string.widget_video) -> toNavigate(
                        this@WidgetFragment,
                        R.id.action_video
                    )

                    getString(R.string.widget_banner) -> toNavigate(
                        this@WidgetFragment,
                        R.id.fragment_home
                    )

                    else -> Log.i(item)
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