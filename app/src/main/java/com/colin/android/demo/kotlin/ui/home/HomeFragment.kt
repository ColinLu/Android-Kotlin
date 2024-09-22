package com.colin.android.demo.kotlin.ui.home

import android.os.Bundle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentHomeBinding
import com.colin.android.demo.kotlin.toNavigate
import com.colin.library.android.utils.L
import com.colin.library.android.utils.onClick


class HomeFragment : AppFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by lazy { createModel(this) }

    override fun initView(savedInstanceState: Bundle?) {
        viewBinding.apply {
            L.e("$this")
            L.d(TAG, "viewBinding.apply")
            buttonPicture.setOnClickListener {
                L.d(TAG, "onClick buttonPicture")
            }
            buttonLog.onClick {
                L.d(TAG, "onClick buttonLog")
                toNavigate(this@HomeFragment, R.id.action_to_log)
            }
        }
    }

    override fun initData(bundle: Bundle?) {
        viewModel.text.observe(this) {
            L.i(TAG, it)
            viewBinding.text.text = it
        }


    }

}