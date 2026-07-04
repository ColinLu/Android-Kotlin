package com.colin.android.demo.kotlin.ui.module

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.ui.method.MethodViewModel
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration

class WidgetsFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy { StringAdapter() }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.isEnabled = false
            list.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@WidgetsFragment.adapter
                addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }

        adapter.submitList(listOf(
            "ClearEditText Test",
            "RoundImageView Test",
            "BannerView Test",
            "WheelView Test"
        ))

        adapter.onItemClickListener = { _, item, _ ->
            ToastUtil.show("Testing Widget: $item")
            // In a real app, we might navigate to specific widget demo screens
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }
}
