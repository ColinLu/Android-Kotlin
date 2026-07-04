package com.colin.android.demo.kotlin.ui.module

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.ui.method.MethodViewModel
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.SpUtil
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration

class UtilsFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy { StringAdapter() }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.isEnabled = false
            list.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@UtilsFragment.adapter
                addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }

        adapter.submitList(listOf(
            "Test Log.i",
            "Test Log.json",
            "Test ToastUtil",
            "Test SpUtil Write",
            "Test SpUtil Read"
        ))

        adapter.onItemClickListener = { _, item, _ ->
            when (item) {
                "Test Log.i" -> Log.i("Utility Log Test")
                "Test Log.json" -> Log.json("{\"name\":\"Colin\", \"age\":18}")
                "Test ToastUtil" -> ToastUtil.show("Toast Test from Utils")
                "Test SpUtil Write" -> {
                    val time = System.currentTimeMillis()
                    SpUtil.put("utils_test_time", time)
                    ToastUtil.show("Saved time: $time")
                }
                "Test SpUtil Read" -> {
                    val time = SpUtil.getLong("utils_test_time", 0L)
                    ToastUtil.show("Read time: $time")
                }
            }
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }
}
