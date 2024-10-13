package com.colin.library.android.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.colin.library.android.base.def.IBase
import com.colin.library.android.utils.L

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :BaseFragment基类:最简单的业务逻辑定义
 */
abstract class BaseFragment : Fragment(), IBase {
    val TAG = this::class.simpleName!!

    var refresh: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        L.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        initData(arguments)
    }

    override fun onStart() {
        L.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        L.d(TAG, "onResume")
        super.onResume()
        if (refresh) {
            refresh = false
            loadData(true)
        }
    }

    override fun onPause() {
        L.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        L.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        L.d(TAG, "onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        L.d(TAG, "onDestroy")
        super.onDestroy()
    }

}