package com.colin.library.android.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.colin.library.android.base.def.IBase

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :BaseFragment基类:最简单的业务逻辑定义
 */
abstract class BaseFragment : Fragment(), IBase {
    var refresh: Boolean = true

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