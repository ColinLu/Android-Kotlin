package com.colin.android.demo.kotlin.app

import android.app.Application
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

        val build = UtilConfig.newBuilder(this).build()
        UtilHelper.isDebug()
    }
}