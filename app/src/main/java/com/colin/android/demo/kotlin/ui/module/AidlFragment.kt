package com.colin.android.demo.kotlin.ui.module

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.client.AIDLClient
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.ui.method.MethodViewModel
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration

class AidlFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy { StringAdapter() }
    private var aidlClient: AIDLClient? = null

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.isEnabled = false
            list.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@AidlFragment.adapter
                addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }

        adapter.submitList(listOf(
            "Bind Service",
            "Unbind Service",
            "Send String: Hello AIDL",
            "Send Item: ID 1, Title 'AIDL Test'",
            "Check Status"
        ))

        adapter.onItemClickListener = { _, item, _ ->
            when (item) {
                "Bind Service" -> bindAidl()
                "Unbind Service" -> unbindAidl()
                "Send String: Hello AIDL" -> aidlClient?.stringChanged("Hello AIDL from Fragment")
                "Send Item: ID 1, Title 'AIDL Test'" -> aidlClient?.itemChanged(ItemBean(1, "AIDL Test"))
                "Check Status" -> aidlClient?.aidlStatus(true)
            }
        }
    }

    private fun bindAidl() {
        if (aidlClient == null) {
            aidlClient = AIDLClient(requireActivity().application, object : AIDLClient.Callback {
                override fun aidlStatus(isConnected: Boolean) {
                    Log.i("AIDL Status: $isConnected")
                    ToastUtil.show("AIDL Connected: $isConnected")
                }

                override fun aidlChanged(data: String?) {
                    Log.i("AIDL Data Changed: $data")
                    ToastUtil.show("AIDL Received: $data")
                }

                override fun itemChanged(itemBean: ItemBean?) {
                    Log.i("AIDL Item Changed: $itemBean")
                    ToastUtil.show("AIDL Received Item: $itemBean")
                }
            })
        }
        aidlClient?.bindService()
        ToastUtil.show("Binding Service...")
    }

    private fun unbindAidl() {
        aidlClient?.unbindService()
        ToastUtil.show("Unbinding Service...")
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        aidlClient?.unbindService()
    }
}
