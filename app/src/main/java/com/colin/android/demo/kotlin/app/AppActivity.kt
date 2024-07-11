package com.colin.android.demo.kotlin.app

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.colin.library.android.base.BaseActivity

abstract class AppActivity<VB : ViewBinding, VM : ViewModel> : BaseActivity() {
    abstract val binding: VB
    abstract val model: VM

    override val layoutRes: Int = Resources.ID_NULL

    override val rootView: View
        get() = binding.root


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView, savedInstanceState)
    }

    override fun loadData(refresh: Boolean) {

    }
}



