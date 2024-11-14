package com.colin.android.demo.kotlin.app

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.helper.UtilConfig
import com.colin.library.android.utils.helper.UtilHelper

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :TODO
 */
class App : Application() {

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        UtilHelper.init(UtilConfig.newBuilder(this, true).build())
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeObserver())
    }

    private inner class AppLifeObserver : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (Lifecycle.Event.ON_START == event) {
                Log.e("foreground")
            } else if (Lifecycle.Event.ON_STOP == event) {
                Log.e("background")
            }
        }


    }
}