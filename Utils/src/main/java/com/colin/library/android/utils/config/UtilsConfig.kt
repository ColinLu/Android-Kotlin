package com.colin.library.android.utils.config

import android.app.Application
import android.util.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-18
 *
 * Des   :工具类配置
 * 1.app上下文、全局配置开关
 * 2.Log配置
 */
class UtilConfig private constructor(builder: Builder) {
    /*全局上下文*/
    private val mApplication = builder.mApplication

    /*环境*/
    private val mDebug = builder.mDebug     //是否测试环境
    private val mShowLog: Boolean           //Log是否需要显示
    private val mLogLevel: Int              //Log默认显示Level，Level等级一下不会显示
    private val mLogTag: String?            //Log配置全局Tag

    init {
        this.mShowLog = builder.mShowLog
        this.mLogLevel = builder.mLogLevel
        this.mLogTag = builder.mLogTag
    }

    fun getApplication(): Application {
        return mApplication
    }

    fun isDebug(): Boolean {
        return mDebug
    }

    fun isShowLog(): Boolean {
        return mShowLog
    }


    fun getLogLevel(): Int {
        return mLogLevel
    }

    fun getLogTag(): String? {
        return mLogTag
    }

    internal fun canShowLog(level: Int) = mShowLog && level >= mLogLevel

    class Builder @JvmOverloads constructor(/*全局上下文*/internal val mApplication: Application, /*环境*/
                                            val mDebug: Boolean = true
    ) {
        /*Log*/
        var mShowLog: Boolean = true
        var mLogLevel: Int = Log.INFO
        var mLogTag: String? = null

        init {
            this.mShowLog = mDebug
        }

        fun setShowLog(show: Boolean): Builder {
            this.mShowLog = show
            return this
        }

        fun setLogLevel(level: Int): Builder {
            this.mLogLevel = level
            return this
        }

        fun setLogTag(tag: String?): Builder {
            this.mLogTag = tag
            return this
        }

        fun build(): UtilConfig {
            return UtilConfig(this)
        }
    }


    companion object {
        fun newBuilder(application: Application, debug: Boolean = true): Builder {
            return Builder(application, debug)
        }
    }
}