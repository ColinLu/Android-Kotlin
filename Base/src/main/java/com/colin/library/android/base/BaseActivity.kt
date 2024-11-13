package com.colin.library.android.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.colin.library.android.base.def.IBase
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.helper.LanguageHelper
import java.util.Locale
import java.util.Objects

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :Activity基类:最简单的业务逻辑定义
 */
abstract class BaseActivity : AppCompatActivity(), IBase {
    val TAG = this::class.simpleName!!
    var refresh: Boolean = true

    override fun attachBaseContext(newBase: Context) {
        if (checkLanguage()) {
            val locale = LanguageHelper.instance.getCurrentLocal()
            if (Objects.equals(Locale.getDefault(), locale).not()) {
                val config = newBase.resources.configuration
                config.setLocale(locale)
                super.attachBaseContext(newBase.createConfigurationContext(config))
            } else super.attachBaseContext(newBase)
        } else super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        super.onRestart()
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        if (refresh) {
            refresh = false
            loadData(true)
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
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

    /*界面初始化的时候，是否需要检查语言*/
    open fun checkLanguage() = false
}