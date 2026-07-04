package com.colin.library.android.widget.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 * Des   :BaseFragment基类 - 核心生命周期管理
 */
abstract class BaseFragment : Fragment(), IBase, ILoad {
    protected val TAG: String by lazy { this::class.java.simpleName }
    protected var isFirstLoad: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (UtilHelper.isDebug()) Log.d(TAG, "Lifecycle: onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        initView(arguments, savedInstanceState)
        initData(arguments, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (UtilHelper.isDebug()) Log.d(TAG, "Lifecycle: onResume")
        if (isFirstLoad) {
            isFirstLoad = false
            loadData(true)
        }
    }

    override fun onDestroyView() {
        if (UtilHelper.isDebug()) Log.d(TAG, "Lifecycle: onDestroyView")
        super.onDestroyView()
    }

    override fun goBack(): Boolean = false

    override fun loadData(refresh: Boolean) {
        // 子类可选实现
    }
}
