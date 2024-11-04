package com.colin.android.demo.kotlin.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import com.colin.android.demo.kotlin.IAidlRemoteCallback
import com.colin.android.demo.kotlin.IDemoAidlInterface
import com.colin.android.demo.kotlin.def.ItemBean

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-29
 *
 * Des   :TODO
 */
class DemoAidlService : Service() {

    override fun onBind(intent: Intent?): IBinder {
        return bind
    }

    private val callbackList = RemoteCallbackList<IAidlRemoteCallback>()
    private val bind = object : IDemoAidlInterface.Stub() {
        override fun register(callback: IAidlRemoteCallback?) {
            callback?.let { callbackList.register(it) }
        }

        override fun unregister(callback: IAidlRemoteCallback?) {
            callback?.let { callbackList.unregister(it) }
        }

        override fun stringChanged(string: String?) {
            string?.let {
                val size = callbackList.beginBroadcast()
                for (i in 0 until size) {
                    callbackList.getBroadcastItem(i).aidlChanged(it)
                }
                callbackList.finishBroadcast()
            }
        }

        override fun itemChanged(itembean: ItemBean?) {
            itembean?.let {
                val size = callbackList.beginBroadcast()
                for (i in 0 until size) {
                    callbackList.getBroadcastItem(i).itemChanged(it)
                }
                callbackList.finishBroadcast()
            }
        }

    }
}