package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.ApiService
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.library.android.network.NetworkHelper
import com.colin.library.android.network.request
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration

class NetworkFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy { StringAdapter() }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.isEnabled = false
            list.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@NetworkFragment.adapter
                addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }

        adapter.submitList(listOf(
            "Init Network (BaseUrl)",
            "Send Login SMS (Test Request)",
            "Test Network Status"
        ))

        adapter.onItemClickListener = { _, item, _ ->
            when (item) {
                "Init Network (BaseUrl)" -> {
                    NetworkHelper.baseUrl = "https://api.example.com/"
                    ToastUtil.show("Network Initialized with dummy URL")
                }
                "Send Login SMS (Test Request)" -> testNetworkRequest()
                "Test Network Status" -> {
                    Log.i("Network testing...")
                    ToastUtil.show("Check Logcat for details")
                }
            }
        }
    }

    private fun testNetworkRequest() {
        // Note: This will likely fail with a real URL or dummy one, but demonstrates usage
        viewModel.request(
            request = { NetworkHelper.create<ApiService>().login("13800138000") },
            result = { success ->
                Log.i("Login SMS request result: $success")
                ToastUtil.show("Request Success: $success")
            },
            state = { code, msg ->
                Log.e("Request Error: $code, $msg")
                ToastUtil.show("Error: $msg")
            }
        )
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }
}
