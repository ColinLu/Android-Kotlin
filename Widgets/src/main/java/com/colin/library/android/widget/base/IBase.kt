package com.colin.library.android.widget.base

import android.os.Bundle
import androidx.annotation.LayoutRes

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-23
 *
 * Des   :Customize the interface initialization action
 */
interface IBase {
    @LayoutRes
    fun layoutRes(): Int = 0

    /*init default view(findView、listener)*/
    fun initView(bundle: Bundle?, savedInstanceState: Bundle?)

    /*Previous interface data、bind liveData*/
    fun initData(bundle: Bundle?, savedInstanceState: Bundle?)

    fun goBack(): Boolean

}