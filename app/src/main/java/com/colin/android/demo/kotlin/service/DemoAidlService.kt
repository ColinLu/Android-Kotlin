package com.colin.android.demo.kotlin.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import com.colin.android.demo.kotlin.IDemoAidlCallback
import com.colin.android.demo.kotlin.IDemoAidlInterface
import com.colin.android.demo.kotlin.def.ItemBean
import com.colin.library.android.utils.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-10-29
 *
 * Des   :AIDL 服务端实现
 *
 * 1.
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
 *
 * 2.
 * private val connection = object : ServiceConnection {
 *     override fun onServiceConnected(name: ComponentName, service: IBinder) {
 *         aidlService = IDemoAidlInterface.Stub.asInterface(service)
 *         aidlService!!.register(callback)
 *     }
 *
 *     override fun onServiceDisconnected(name: ComponentName?) {
 *          aidlService?.register(callback)
 *          aidlService = null
 *     }
 * }
 * 3.
 * private const val AIDL_ACTION = "com.colin.android.demo.kotlin.service.DemoAidlService"
 * private const val AIDL_PACKAGE = "com.colin.android.demo.kotlin"
 * val intent = Intent(AIDL_ACTION)
 * intent.`package` = AIDL_PACKAGE
 * val result = context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
 *
 * 4.
 * 其他app端实现记得在配置清单添加当前app包名查询标签
 *   <queries>
 *         <package android:name="com.colin.android.demo.kotlin" />
 *   </queries>
 */
class DemoAidlService : Service() {
    private val callbackList by lazy { RemoteCallbackList<IDemoAidlCallback>() }

    override fun onBind(intent: Intent?): IBinder {
        Log.e("onBind")
        return bind
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("onStartCommand")
        // 防止服务被系统杀死后自动重启
        return START_NOT_STICKY
    }

    private val bind = object : IDemoAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun register(callback: IDemoAidlCallback?) {
            Log.e("register IDemoAidlCallback")
            callback?.let { callbackList.register(it) }
            aidlStatus(true)
        }

        @Throws(RemoteException::class)
        override fun unregister(callback: IDemoAidlCallback?) {
            Log.e("unregister IDemoAidlCallback")
            callback?.let { callbackList.unregister(it) }
            aidlStatus(false)
        }

        @Throws(RemoteException::class)
        override fun aidlStatus(isConnected: Boolean) {
            Log.e("aidlStatus isConnected:$isConnected")
            val size = callbackList.beginBroadcast()
            for (i in 0 until size) {
                callbackList.getBroadcastItem(i).aidlStatus(isConnected)
            }
            callbackList.finishBroadcast()
        }

        @Throws(RemoteException::class)
        override fun stringChanged(string: String?) {
            Log.e("stringChanged string:$string")
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
            Log.e("itemChanged item:$item")
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