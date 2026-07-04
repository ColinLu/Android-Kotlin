package com.colin.android.demo.kotlin.ui.method.aidl

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.client.AIDLClient
import com.colin.android.demo.kotlin.databinding.FragmentAidlClientBinding
import com.colin.android.demo.kotlin.ui.method.ModuleViewModel
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.utils.ext.onClick

class AidlClientFragment : AppFragment<FragmentAidlClientBinding, ModuleViewModel>() {
    private val callback = object : AIDLClient.Callback {
        override fun aidlStatus(isConnected: Boolean) {
            Log.i("aidlStatus $isConnected")
            showToast("aidlStatus $isConnected")
        }


        override fun aidlChanged(data: String?) {
            Log.i("aidlChanged $data")
            showToast("aidlChanged $data")

        }

        override fun itemChanged(itemBean: ItemBean?) {
            Log.i("itemChanged $itemBean")
            showToast("itemChanged $itemBean")
        }

    }
    private val aidlClient by lazy {
        AIDLClient(requireContext(), callback)
    }

    override fun onDestroyView() {
        aidlClient.unbindService()
        super.onDestroyView()
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.status.onClick {
            val status = aidlClient.isConnected()
            Log.i("isConnected $status")
            showToast("isConnected $status")
        }
        viewBinding.bind.onClick {
            aidlClient.bindService()
        }
        viewBinding.unbind.onClick {
            aidlClient.unbindService()
        }

    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun loadData(refresh: Boolean) {

    }

    private fun showToast(toast: String) {
        ContextCompat.getMainExecutor(requireContext()).execute {
            ToastUtil.show(toast)
        }
    }


}