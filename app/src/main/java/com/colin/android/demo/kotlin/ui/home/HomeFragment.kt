package com.colin.android.demo.kotlin.ui.home

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentBinding

class HomeFragment : AppFragment<FragmentBinding, HomeViewModel>() {

    override val model: HomeViewModel by lazy { createModel(this) }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData(bundle: Bundle?) {
        model.text.observe(this) {
            binding.text.text = it
        }
    }
}