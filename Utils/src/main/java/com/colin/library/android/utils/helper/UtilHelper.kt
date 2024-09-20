package com.colin.library.android.utils.helper

import android.app.Application

object UtilHelper {
    private lateinit var app: Application
    private var isDebug = false

    fun init(application: Application, isDebug: Boolean) {
        this.app = application
        this.isDebug = isDebug
    }

    /**
     * 获取全局应用
     */
    fun getApplication() = app

    /**
     * 是否为debug环境
     */
    fun isDebug() = isDebug

    fun <T> getSystemService(clazz: Class<T>) = app.getSystemService(clazz)
}