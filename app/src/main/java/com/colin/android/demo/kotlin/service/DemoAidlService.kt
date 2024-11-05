package com.colin.android.demo.kotlin.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import com.colin.android.demo.kotlin.IAidlRemoteCallback
import com.colin.android.demo.kotlin.IDemoAidlInterface
import com.colin.android.demo.kotlin.def.ItemBean

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-29
 *
 * Des   :AIDL 实现
 */
class DemoAidlService : Service() {

    override fun onBind(intent: Intent?): IBinder {
        return bind
    }

    private val callbackList = RemoteCallbackList<IAidlRemoteCallback>()
    private val bind = object : IDemoAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun register(callback: IAidlRemoteCallback?) {
            callback?.let { callbackList.register(it) }
        }

        @Throws(RemoteException::class)
        override fun unregister(callback: IAidlRemoteCallback?) {
            callback?.let { callbackList.unregister(it) }
        }

        @Throws(RemoteException::class)
        override fun stringChanged(string: String?) {
            string?.let {
                val size = callbackList.beginBroadcast()
                for (i in 0 until size) {
                    callbackList.getBroadcastItem(i).aidlChanged(it)
                }
                callbackList.finishBroadcast()
            }
        }

        @Throws(RemoteException::class)
        override fun itemChanged(item: ItemBean?) {
            item?.let {
                val size = callbackList.beginBroadcast()
                for (i in 0 until size) {
                    callbackList.getBroadcastItem(i).itemChanged(it)
                }
                callbackList.finishBroadcast()
            }
        }

    }
}