package com.colin.android.demo.kotlin.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.colin.android.demo.kotlin.IAIDLCallback
import com.colin.android.demo.kotlin.IAIDLInterface
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.service.AIDLService

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-10-18 16:58
 *
 * Des   :AIDLClient
 */
class AIDLClient(
    private val context: Context, private val callback: Callback
) {
    companion object {
        private const val TAG = "AIDLClient"
    }

    private var aidlService: IAIDLInterface? = null
//    private val callback = object : IAIDLCallback.Stub() {
//        override fun aidlStatus(isConnected: Boolean) {
//            Log.i(TAG, "aidlStatus:$isConnected")
//        }
//
//        override fun aidlChanged(data: String?) {
//            Log.i(TAG, "aidlChanged:$data")
//        }
//
//        override fun itemChanged(itembean: ItemBean?) {
//            Log.i(TAG, "itemChanged:$itembean")
//        }
//
//    }


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            aidlService = IAIDLInterface.Stub.asInterface(service)
            aidlService?.register(aidlCallback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService?.unregister(aidlCallback)
            aidlService = null
        }
    }

    fun bindService(`package`: String = "com.colin.android.demo.kotlin") {
        val intent = Intent(AIDLService.ACTION).apply {
            this.`package` = `package`
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        context.unbindService(connection)
    }

    private val aidlCallback = object : IAIDLCallback.Stub() {
        override fun aidlStatus(isConnected: Boolean) {
            callback.aidlStatus(isConnected)
        }

        override fun aidlChanged(data: String?) {
            callback.aidlChanged(data)
        }

        override fun itemChanged(itembean: ItemBean?) {
            callback.itemChanged(itembean)
        }
    }

    interface Callback {
        fun aidlStatus(isConnected: Boolean)
        fun aidlChanged(data: String?)
        fun itemChanged(itemBean: ItemBean?)
    }


}