package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.library.android.utils.L
import kotlinx.coroutines.launch


class MethodFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {

    override val viewModel = createModel(this, MethodViewModel::class.java)
    val adapter by lazy { StringAdapter() }
    override fun initView(savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            layoutList.list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@MethodFragment.adapter
            }
            adapter.onItemClickListener = { _, item ->
                L.e(item)
            }
        }
    }

    override fun initData(bundle: Bundle?) {
        lifecycleScope.launch {
            viewModel.list.flowWithLifecycle(lifecycle).collect {
                L.i(it)
                it.apply {
                    adapter.setData(it)
                }
            }
        }

    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData()
    }


}