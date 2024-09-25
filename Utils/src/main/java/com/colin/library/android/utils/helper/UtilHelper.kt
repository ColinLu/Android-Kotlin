package com.colin.library.android.utils.helper

import android.app.Application


object UtilHelper {
    private lateinit var config: UtilConfig

    fun init(app: Application, debug: Boolean = true) {
        init(UtilConfig.newBuilder(app, debug).build())
    }

    fun init(config: UtilConfig) {
        this.config = config
    }


    /**
     * 获取全局应用
     */
    fun getApplication() = config.getApplication()

    /**
     * 是否为debug环境
     */
    fun isDebug() = config.isDebug()

    fun <T> getSystemService(clazz: Class<T>) = config.getApplication().getSystemService(clazz)
}