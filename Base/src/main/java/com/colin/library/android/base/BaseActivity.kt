package com.colin.library.android.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity


abstract class BaseActivity : AppCompatActivity() {
    var refresh: Boolean = true

    @get:LayoutRes
    abstract val layoutRes: Int

    abstract val rootView: View

    fun setContentView(layoutRes: Int, savedInstanceState: Bundle?) {
        super.setContentView(layoutRes)
        initView(savedInstanceState)
        initData(intent?.extras)
    }

    fun setContentView(view: View?, savedInstanceState: Bundle?) {
        super.setContentView(view)
        initView(savedInstanceState)
        initData(null)
    }

    override fun onResume() {
        super.onResume()
        if (refresh) {
            refresh = false
            loadData(true)
        }
    }

    /*init default view(findView、listener)*/
    abstract fun initView(savedInstanceState: Bundle?)

    /*init default data*/
    abstract fun initData(bundle: Bundle?)

    /*load data by sqlite、http*/
    abstract fun loadData(refresh: Boolean)
}