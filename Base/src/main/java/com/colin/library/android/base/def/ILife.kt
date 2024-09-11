package com.colin.library.android.base.def

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :生命周期
 */
interface ILife : LifecycleObserver {
    fun getContext(): Context

    fun getLifecycle(): Lifecycle
}