package com.colin.library.android.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.colin.library.android.utils.L
import java.lang.ref.WeakReference

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :仅适用于动态广播，静态广播不适用
 */
abstract class BaseReceiver(listener: OnReceiverListener) : BroadcastReceiver(),
    DefaultLifecycleObserver {
    var listenerRef: WeakReference<out OnReceiverListener> = WeakReference(listener)

    init {
        listener.lifecycle.addObserver(this)
    }

    @SuppressLint("InlinedApi")
    override fun onCreate(owner: LifecycleOwner) {
        val context = listenerRef.get()?.getContext() ?: return
        L.d("BroadcastReceiver registerReceiver $this")
        context.registerReceiver(this, getIntentFilter(), Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        val context = listenerRef.get()?.getContext() ?: return
        L.d("BroadcastReceiver unregisterReceiver $this")
        context.unregisterReceiver(this)
    }

    /*子类重写，进行动作捕获监听*/
    abstract fun getIntentFilter(): IntentFilter?

}


interface OnReceiverListener : LifecycleOwner {
    fun getContext(): Context?
}