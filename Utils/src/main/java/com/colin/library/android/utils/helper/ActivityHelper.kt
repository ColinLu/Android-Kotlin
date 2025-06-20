package com.colin.library.android.utils.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Activity管理类
 */
object ActivityHelper {

    private val tasks = mutableListOf<Activity>()

    fun push(activity: Activity) {
        tasks.add(activity)
    }

    fun pop(activity: Activity) {
        tasks.remove(activity)
    }

    fun top(): Activity? {
        return tasks.last()
    }

    fun finishAllActivity(callback: (() -> Unit)? = null) {
        val it = tasks.iterator()
        while (it.hasNext()) {
            val item = it.next()
            it.remove()
            item.finish()
        }
        callback?.invoke()
    }

    /**
     * 关闭其他activity
     */
    fun finishOtherActivity(clazz: Class<out Activity>, callback: (() -> Unit)? = null) {
        val it = tasks.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item::class.java != clazz) {
                it.remove()
                item.finish()
            }
        }
        callback?.invoke()
    }


    /**
     * 关闭activity
     */
    fun finishActivity(clazz: Class<out Activity>) {
        val it = tasks.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item::class.java == clazz) {
                it.remove()
                item.finish()
                break
            }
        }
    }

    /**
     * activity是否在栈中
     */
    fun isActivityExists(clazz: Class<out Activity>?): Boolean {
        if (clazz != null) {
            for (task in tasks) {
                if (task::class.java == clazz) return true
            }
        }
        return false
    }

    /**
     * Activity是否销毁
     * @param activity
     */
    fun isActivityDestroy(activity: Activity): Boolean {
        return activity.isDestroyed || activity.isFinishing
    }

    fun isActivityDestroy(context: Context): Boolean {
        val activity = findActivity(context)
        return if (activity != null) {
            activity.isDestroyed || activity.isFinishing
        } else true
    }

    /**
     * ContextWrapper是context的包装类，AppcompatActivity，service，application实际上都是ContextWrapper的子类
     * AppcompatXXX类的context都会被包装成TintContextWrapper
     * @param context
     */
    private fun findActivity(context: Context): Activity? {
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return findActivity(context.baseContext)
        }
        return null
    }
}