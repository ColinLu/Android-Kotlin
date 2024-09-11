package com.colin.library.android.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.colin.library.android.base.def.IBase

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :Activity基类:最简单的业务逻辑定义
 */
abstract class BaseActivity : AppCompatActivity(), IBase {
    var refresh: Boolean = true

    open fun setContentView(layoutRes: Int, savedInstanceState: Bundle?) {
        super.setContentView(layoutRes)
        initView(savedInstanceState)
        initData(intent?.extras)
    }

    fun setContentView(view: View, savedInstanceState: Bundle?) {
        super.setContentView(view)
        initView(savedInstanceState)
        initData(intent?.extras)
    }

    override fun onResume() {
        super.onResume()
        if (refresh) {
            refresh = false
            loadData(true)
        }
    }
}