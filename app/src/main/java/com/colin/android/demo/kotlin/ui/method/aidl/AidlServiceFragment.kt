package com.colin.android.demo.kotlin.ui.method.aidl

import android.content.Intent
import android.os.Bundle
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentAidlServiceBinding
import com.colin.android.demo.kotlin.service.AIDLService
import com.colin.library.android.utils.ext.onClick

class AidlServiceFragment : AppFragment<FragmentAidlServiceBinding, AidlViewModel>() {
    companion object {
        const val ACTION_SEND_STRING = "action.aidl.send.string"
        const val ACTION_SEND_ITEM = "action.aidl.send.item"
        const val KEY_SEND_VALUE = "KEY_SEND_VALUE"
    }

    var sendIndex = 0
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.sendText.onClick {
            val intent = Intent(requireContext(), AIDLService::class.java).apply {
                action = ACTION_SEND_STRING
                putExtra(KEY_SEND_VALUE, "sendText:$sendIndex")
            }
            requireContext().startService(intent)
        }
        viewBinding.sendObject.onClick {
            sendIndex += 1
            val intent = Intent(requireContext(), AIDLService::class.java).apply {
                action = ACTION_SEND_ITEM
                putExtra(KEY_SEND_VALUE, ItemBean(sendIndex, "sendIndex"))
            }
            requireContext().startService(intent)
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {

    }


}