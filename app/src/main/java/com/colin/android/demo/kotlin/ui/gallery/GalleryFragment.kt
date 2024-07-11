package com.colin.android.demo.kotlin.ui.gallery

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentBinding

class GalleryFragment : AppFragment<FragmentBinding, GalleryViewModel>() {

    override val model: GalleryViewModel by lazy { createModel(this) }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData(bundle: Bundle?) {
        model.text.observe(this) {
            binding.text.text = it
        }
    }
}