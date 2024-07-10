package com.colin.library.android.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment


abstract class BaseFragment : Fragment() {
    var refresh: Boolean = true

    @get:LayoutRes
    abstract val layoutRes: Int

    abstract val rootView: View
    /*init default view(findView、listener)*/
    abstract fun initView(savedInstanceState: Bundle?)

    /*init default data*/
    abstract fun initData(bundle: Bundle?)

    /*load data by sqlite、http*/
    abstract fun loadData(refresh: Boolean)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initData(arguments)
    }

    override fun onResume() {
        super.onResume()
        if (refresh) {
            refresh = false
            loadData(true)
        }
    }

}