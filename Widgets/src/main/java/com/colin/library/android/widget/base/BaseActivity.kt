package com.colin.library.android.widget.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :Activity基类 - 核心生命周期与通用行为管理
 */
abstract class BaseActivity : AppCompatActivity(), IBase, ILoad {
    protected val TAG: String by lazy { this::class.java.simpleName }
    protected var isFirstLoad: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UtilHelper.isDebug()) Log.d(TAG, "Lifecycle: onCreate")
        super.onCreate(savedInstanceState)
        
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!goBack()) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (UtilHelper.isDebug()) Log.d(TAG, "Lifecycle: onResume")
        if (isFirstLoad) {
            isFirstLoad = false
            loadData(true)
        }
    }

    override fun onDestroy() {
        if (UtilHelper.isDebug()) Log.d(TAG, "Lifecycle: onDestroy")
        super.onDestroy()
    }

    /**
     * 设置内容视图并初始化
     */
    fun setContentView(layoutResID: Int, savedInstanceState: Bundle?) {
        super.setContentView(layoutResID)
        initView(intent?.extras, savedInstanceState)
        initData(intent?.extras, savedInstanceState)
    }

    /**
     * 处理返回逻辑，默认关闭当前页面
     * @return true 表示已处理，false 表示交给系统处理
     */
    override fun goBack(): Boolean {
        finish()
        return true
    }

    override fun loadData(refresh: Boolean) {
        // 子类可选实现
    }
}
