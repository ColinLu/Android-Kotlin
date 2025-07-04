package com.colin.android.demo.kotlin.ui.method

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.toNavigate
import com.colin.android.demo.kotlin.ui.list.ListFragment
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.launch


class MethodFragment : AppFragment<LayoutRefreshListBinding, MethodViewModel>() {
    private val adapter by lazy {
        StringAdapter().apply {
            empty = R.layout.layout_empty
        }
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    val launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            Log.d("requestPermission granted:${result}")
            ToastUtil.show("permission:$result")
        }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            list.apply {
                this.adapter = this@MethodFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, _ ->
                when (item) {
                    getString(R.string.method_log) -> {
                        toNavigate(this@MethodFragment, R.id.action_to_log)
                    }

                    getString(R.string.method_path) -> {
                        ListFragment.toNavigate(this@MethodFragment, R.array.path_list)
                    }

                    getString(R.string.method_permission) -> {
                        launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    getString(R.string.method_command) -> {
                        toNavigate(this@MethodFragment, R.id.action_command)
                    }

                    getString(R.string.method_crash) -> {
                        throw RuntimeException("test crash")
                    }

                    getString(R.string.method_contact) -> {
                        ToastUtil.show(R.string.method_contact)
                    }

                    else -> {
                        Log.i(item)
                    }
                }
            }
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.list.flowWithLifecycle(lifecycle).collect {
                    Log.i("list:$it", it)
                    adapter.submitList(it)
                    viewBinding.refresh.isRefreshing = false
                }
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData()
    }


}