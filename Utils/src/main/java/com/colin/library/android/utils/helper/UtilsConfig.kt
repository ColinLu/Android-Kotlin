package com.colin.library.android.utils.helper

import android.app.Application
import com.colin.library.android.utils.LogLevel

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-18
 *
 * Des   :TODO
 */
class UtilConfig private constructor(builder: Builder) {
    /*全局上下文*/
    private val mApplication = builder.mApplication

    /*环境*/
    private val mDebug = builder.mDebug
    private val mShowLog: Boolean
    private val mShowLogThread: Boolean
    private val mLogLevel: Int

    /*show 指定tag log*/
    private val mLogTag: String?
    private val mLogMethodOffset: Int
    private val mLogMethodCount: Int

    init {
        this.mShowLog = builder.mShowLog
        this.mShowLogThread = builder.mShowLogThread
        this.mLogLevel = builder.mLogLevel
        this.mLogTag = builder.mLogTag
        this.mLogMethodCount = builder.mLogMethodCount
        this.mLogMethodOffset = builder.mLogMethodOffset
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

    fun isShowLogThread(): Boolean {
        return mShowLogThread
    }

    fun getLogMethodOffset(): Int {
        return mLogMethodOffset
    }

    fun getLogMethodCount(): Int {
        return mLogMethodCount
    }

    @LogLevel
    fun getLogLevel(): Int {
        return mLogLevel
    }

    fun getLogTag(): String? {
        return mLogTag
    }

    class Builder @JvmOverloads constructor(/*全局上下文*/internal val mApplication: Application, /*环境*/
                                            val mDebug: Boolean = true
    ) {
        /*Log*/
        var mShowLog: Boolean = true
        var mShowLogThread: Boolean = true

        @LogLevel
        var mLogLevel: Int = LogLevel.I
        var mLogTag: String? = null
        var mLogMethodOffset: Int = 0
        var mLogMethodCount: Int = 3

        init {
            this.mShowLog = mDebug
        }

        fun setShowLog(show: Boolean): Builder {
            this.mShowLog = show
            return this
        }

        fun setShowLogThread(show: Boolean): Builder {
            this.mShowLogThread = show
            return this
        }

        fun setLogLevel(@LogLevel level: Int): Builder {
            this.mLogLevel = level
            return this
        }

        fun setLogTag(tag: String?): Builder {
            this.mLogTag = tag
            return this
        }

        fun setLogMethodOffset(offset: Int): Builder {
            this.mLogMethodOffset = offset
            return this
        }

        fun setLogMethodCount(count: Int): Builder {
            this.mLogMethodCount = count
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