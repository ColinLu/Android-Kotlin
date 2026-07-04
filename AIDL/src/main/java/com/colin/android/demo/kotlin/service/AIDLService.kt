package com.colin.android.demo.kotlin.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log
import androidx.core.os.BundleCompat
import com.colin.android.demo.kotlin.IAIDLCallback
import com.colin.android.demo.kotlin.IAIDLInterface
import com.colin.android.demo.kotlin.ItemBean

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-10-18 16:57
 *
 * Des   :AIDLService
 */
class AIDLService : Service() {
    companion object {
        private const val TAG = "AIDLService"
        const val ACTION = "com.colin.android.demo.kotlin.service.AIDLService"
        const val ACTION_SEND_STRING = "action.aidl.send.string"
        const val ACTION_SEND_ITEM = "action.aidl.send.item"
        const val KEY_SEND_VALUE = "KEY_SEND_VALUE"
    }

    private val callbackList by lazy { RemoteCallbackList<IAIDLCallback>() }
    private var isConnected = false
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate AIDLService")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.i(TAG, "onBind AIDLService")
        return bind
    }


    override fun onDestroy() {
        Log.i(TAG, "onDestroy AIDLService")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.i(TAG, "onStartCommand AIDLService action:$action")
        if (ACTION_SEND_STRING == action) {
            val value = intent.extras?.getString(KEY_SEND_VALUE) ?: ""
            bind.stringChanged(value)
        }
        if (ACTION_SEND_ITEM == action) {
            val item =
                BundleCompat.getParcelable(intent.extras!!, KEY_SEND_VALUE, ItemBean::class.java)
            bind.itemChanged(item)
        }
        // 防止服务被系统杀死后自动重启
        return START_NOT_STICKY
    }

    private val bind = object : IAIDLInterface.Stub() {
        @Throws(RemoteException::class)
        override fun register(callback: IAIDLCallback?) {
            Log.i(TAG, "register IAIDLCallback")
            callback?.let { callbackList.register(it) }
            isConnected = true
            aidlStatus(isConnected)
        }

        @Throws(RemoteException::class)
        override fun unregister(callback: IAIDLCallback?) {
            Log.i(TAG, "unregister IAIDLCallback")
            isConnected = false
            callback?.let { callbackList.unregister(it) }
            aidlStatus(isConnected)
        }

        override fun getAidlStatus(): Boolean {
            Log.i(TAG, "getAidlStatus isConnected:$isConnected")
            return isConnected
        }

        @Throws(RemoteException::class)
        override fun aidlStatus(isConnected: Boolean) {
            Log.i(TAG, "aidlStatus isConnected:$isConnected")
            val size = callbackList.beginBroadcast()
            for (i in 0 until size) {
                callbackList.getBroadcastItem(i).aidlStatus(isConnected)
            }
            callbackList.finishBroadcast()
        }

        @Throws(RemoteException::class)
        override fun stringChanged(string: String?) {
            Log.i(TAG, "stringChanged string:$string")
            val size = callbackList.beginBroadcast()
            for (i in 0 until size) {
                callbackList.getBroadcastItem(i).aidlChanged(string)
            }
            callbackList.finishBroadcast()
        }

        @Throws(RemoteException::class)
        override fun itemChanged(item: ItemBean?) {
            Log.i(TAG, "itemChanged item:$item")
            val size = callbackList.beginBroadcast()
            for (i in 0 until size) {
                callbackList.getBroadcastItem(i).itemChanged(item)
            }
            callbackList.finishBroadcast()
        }

    }
}