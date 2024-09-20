package com.colin.library.android.base

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.colin.library.android.base.def.OnReceiverListener
import java.lang.ref.SoftReference

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-11
 *
 * Des   :仅适用于动态广播，静态广播不适用
 */
abstract class BaseReceiver(private val listener: OnReceiverListener) : BroadcastReceiver(),
    DefaultLifecycleObserver {
    val listenerRef: SoftReference<out OnReceiverListener> = SoftReference(listener)

    override fun onCreate(owner: LifecycleOwner) {
        listenerRef.get()?.getContext()?.registerReceiver(this, getIntentFilter())
    }

    override fun onDestroy(owner: LifecycleOwner) {
        listenerRef.get()?.getContext()?.unregisterReceiver(this)
    }

    abstract fun getIntentFilter(): IntentFilter?

}