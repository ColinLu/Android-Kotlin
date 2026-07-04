package com.colin.android.demo.kotlin.ui.module

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.ui.method.MethodViewModel
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import com.colin.nfc.focus.NFCFocus
import com.colin.nfc.focus.api.TriggerType
import kotlinx.coroutines.launch

class NfcFocusFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy { StringAdapter() }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.isEnabled = false
            list.apply {
                layoutManager = LinearLayoutManager(requireActivity())
                adapter = this@NfcFocusFragment.adapter
                addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }

        adapter.submitList(listOf(
            "Check NFC Support",
            "Request Usage Stats Permission",
            "Get Current Focus Session",
            "Stop Focus Mode",
            "Get Today's Stats"
        ))

        adapter.onItemClickListener = { _, item, _ ->
            when (item) {
                "Check NFC Support" -> {
                    val result = NFCFocus.manager.checkNfcSupport()
                    ToastUtil.show("NFC Support: $result")
                }
                "Request Usage Stats Permission" -> {
                    lifecycleScope.launch {
                        val result = NFCFocus.manager.requestUsageStatsPermission(requireActivity())
                        ToastUtil.show("Permission Result: ${result.isSuccess}")
                    }
                }
                "Get Current Focus Session" -> {
                    lifecycleScope.launch {
                        val session = NFCFocus.manager.getCurrentSession()
                        ToastUtil.show("Current Session: ${session?.sceneName ?: "None"}")
                    }
                }
                "Stop Focus Mode" -> {
                    lifecycleScope.launch {
                        val session = NFCFocus.manager.getCurrentSession()
                        if (session != null) {
                            val result = NFCFocus.manager.stopFocus(session.sessionId)
                            ToastUtil.show("Stop Focus: ${result.isSuccess}")
                        } else {
                            ToastUtil.show("No active session")
                        }
                    }
                }
                "Get Today's Stats" -> {
                    lifecycleScope.launch {
                        val stats = NFCFocus.manager.getTodayStats()
                        ToastUtil.show("Today's focus count: ${stats.size}")
                    }
                }
            }
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }
}
