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
 * Des   :AIDL 服务端实现
 *
 * 1.
 * bindService(intent, connection, Context.BIND_AUTO_CREATE)
 * 2.
 * private val connection = object : ServiceConnection {
 *     override fun onServiceConnected(name: ComponentName, service: IBinder) {
 *         aidlService = IDemoAidlInterface.Stub.asInterface(service)
 *         aidlService!!.register(callback)
 *     }
 *
 *     override fun onServiceDisconnected(name: ComponentName?) {
 *
 *     }
 * }
 * 3.
 *  private val callback = object : IAidlRemoteCallback.Stub() {
 *
 *      override fun aidlChanged(data: String?) {
 *          data?.let { Log.i("data:$it") }
 *      }
 *
 *      override fun itemChanged(itembean: ItemBean?) {
 *          itembean?.let { Log.i("itemBean:$it") }
 *      }
 *
 *  }
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