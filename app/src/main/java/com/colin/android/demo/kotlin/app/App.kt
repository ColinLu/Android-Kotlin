package com.colin.android.demo.kotlin.app

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.config.UtilConfig
import com.colin.library.android.utils.helper.UtilHelper
import com.colin.nfc.focus.NFCFocus
import com.tencent.smtt.sdk.QbSdk

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :项目 Android 全局App，配置初始化
 */
class App : Application() {

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        NFCFocus.initialize(this)
        UtilHelper.init(UtilConfig.newBuilder(this, true).build())
        QbSdk.initX5Environment(this, object : QbSdk.PreInitCallback {
            override fun onCoreInitFinished() {
                Log.i("onCoreInitFinished:x5")
            }

            override fun onViewInitFinished(isX5: Boolean) {
                Log.i("onViewInitFinished:isX5:$isX5")
            }

        })
        QbSdk.setDownloadWithoutWifi(true)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeObserver())
    }

    private inner class AppLifeObserver : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (Lifecycle.Event.ON_START == event) {
                Log.i("foreground")
            } else if (Lifecycle.Event.ON_STOP == event) {
                Log.i("background")
            }
        }
    }
}