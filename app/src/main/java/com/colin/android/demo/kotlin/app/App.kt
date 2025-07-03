package com.colin.android.demo.kotlin.app

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.config.UtilConfig
import com.colin.library.android.utils.helper.CrashHelper
import com.colin.library.android.utils.helper.UtilHelper
import com.colin.library.android.web.WebUtil
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsListener

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :项目 Android 全局App，配置初始化
 */
class App : Application(), CrashHelper.OnCrashListener {

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        UtilHelper.init(UtilConfig.newBuilder(this, true).build())
        CrashHelper.init(this)
        WebUtil.init(this, object : TbsListener {
            override fun onDownloadFinish(state: Int) {
                Log.e("onDownloadFinish:$state")
            }

            override fun onInstallFinish(state: Int) {
                Log.e("onInstallFinish:$state")
            }

            override fun onDownloadProgress(progress: Int) {
                Log.e("onDownloadFinish:$progress")
            }

        }, object : PreInitCallback {
            override fun onCoreInitFinished() {
                Log.e("onCoreInitFinished")
            }

            override fun onViewInitFinished(success: Boolean) {
                Log.e("onViewInitFinished:$success")
            }

        })
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

    override fun crash(error: Throwable, info: String) {
        Log.e("info:$info")
    }
}