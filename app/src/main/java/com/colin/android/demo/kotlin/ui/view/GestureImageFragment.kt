package com.colin.android.demo.kotlin.ui.view

import android.os.Bundle
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentGestureImageBinding

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-08 16:31
 *
 * Des   :GestureImageFragment
 */
class GestureImageFragment : AppFragment<FragmentGestureImageBinding, ViewViewMode>() {

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.image.setImageResource(R.mipmap.banner1)
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
    }
}