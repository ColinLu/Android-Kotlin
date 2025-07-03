package com.colin.library.android.utils.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Process
import kotlin.system.exitProcess

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

    /*获取指定的Activity*/
    fun getActivity(clazz: Class<out Activity>): Activity? {
        if (tasks.isEmpty()) return null
        for (activity in tasks) {
            if (activity.javaClass == clazz) return activity
        }
        return null
    }

    fun finish(clazz: Class<out Activity>) {
        if (tasks.isEmpty()) return
        val it = tasks.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item::class.java != clazz) {
                it.remove()
                item.finish()
            }
        }
    }

    fun finishAll() {
        val it = tasks.iterator()
        while (it.hasNext()) {
            val item = it.next()
            it.remove()
            item.finish()
        }
    }

    fun exitApp() {
        try {
            //关闭所有Activity
            finishAll()
            // 杀死该应用进程
            Process.killProcess(Process.myPid())
            //Java方式退出
            exitProcess(0)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    /*通过上下文获取当前绑定的Activity界面*/
    fun getActivity(context: Context?): Activity? {
        context ?: return null
        if (context is Activity) return context
        else if (context is ContextWrapper) getActivity(context.baseContext)
        return null
    }

}