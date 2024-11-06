package com.colin.library.android.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.colin.library.android.base.def.IBase
import com.colin.library.android.utils.L

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :Activity基类:最简单的业务逻辑定义
 */
abstract class BaseActivity : AppCompatActivity(), IBase {
    val TAG = this::class.simpleName!!
    var refresh: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        L.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onRestart() {
        L.d(TAG, "onRestart")
        super.onRestart()
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

    override fun onDestroy() {
        L.d(TAG, "onDestroy")
        super.onDestroy()
    }

    open fun setContentView(layoutRes: Int, savedInstanceState: Bundle?) {
        super.setContentView(layoutRes)
        initView(intent?.extras, savedInstanceState)
        initData(intent?.extras, savedInstanceState)
    }

    fun setContentView(view: View, savedInstanceState: Bundle?) {
        super.setContentView(view)
        initView(intent?.extras, savedInstanceState)
        initData(intent?.extras, savedInstanceState)
    }

}