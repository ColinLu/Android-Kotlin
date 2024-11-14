package com.colin.android.demo.kotlin.ui.home

import android.os.Bundle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentHomeBinding
import com.colin.android.demo.kotlin.dialog.DialogTips
import com.colin.android.demo.kotlin.toNavigate
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.onClick


class HomeFragment : AppFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by lazy { createModel(this) }

    override fun initView(bundle: Bundle?,savedInstanceState: Bundle?) {
        viewBinding.apply {
            buttonMethod.onClick {
                toNavigate(this@HomeFragment, R.id.action_to_method)
            }
        }
    }

    override fun initData(bundle: Bundle?,savedInstanceState: Bundle?) {
        viewModel.text.observe(this) {
            Log.i(TAG, it)
            viewBinding.text.text = it

            DialogTips.newBuilder()
                .title("")
        }


    }

}