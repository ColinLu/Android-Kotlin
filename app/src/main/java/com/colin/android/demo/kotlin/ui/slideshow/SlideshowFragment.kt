package com.colin.android.demo.kotlin.ui.slideshow

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentBinding

class SlideshowFragment : AppFragment<FragmentBinding, SlideshowViewModel>() {

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {

    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewModel.text.observe(this) {
            viewBinding.text.text = it
        }
    }
}