package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import com.colin.nfc.focus.NFCFocus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NfcFocusFragment : AppFragment<LayoutRefreshListBinding, ModuleViewModel>() {
    private val adapter by lazy { StringAdapter() }
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@NfcFocusFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, position ->
                when (item) {
                    "Check NFC Support" -> {
                        val result = NFCFocus.manager.checkNfcSupport()
                        ToastUtil.show("NFC Support: $result")
                    }

                    "Request Usage Stats Permission" -> {
                        lifecycleScope.launch {
                            val result =
                                NFCFocus.manager.requestUsageStatsPermission(requireActivity())
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
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.list.flowWithLifecycle(lifecycle).collect {
                Log.i(it)
                it.apply { adapter.submitList(it) }
                viewBinding.refresh.isRefreshing = false
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadData(R.array.nfc_list)
        }
    }

}
