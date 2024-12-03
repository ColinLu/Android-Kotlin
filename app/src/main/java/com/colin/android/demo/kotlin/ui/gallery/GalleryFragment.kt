package com.colin.android.demo.kotlin.ui.gallery

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentBinding
import com.colin.library.android.utils.onClick

class GalleryFragment : AppFragment<FragmentBinding, GalleryViewModel>() {

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.text.onClick {

        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewModel.text.observe(this) {
            viewBinding.text.text = it
        }
    }
}