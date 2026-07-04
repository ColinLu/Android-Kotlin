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
    private var isBound = false

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

    fun bindService(
        `package`: String = "com.colin.android.demo.kotlin", action: String = AIDLService.ACTION
    ) {
        if (isBound) return
        val intent = Intent(action).apply {
            this.`package` = `package`
        }
        isBound = context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            aidlService?.unregister(aidlCallback)
            context.unbindService(connection)
            isBound = false
            aidlService = null
        }
    }

    fun isConnected() = aidlService?.aidlStatus == true

    fun aidlStatus(isConnected: Boolean) {
        aidlService?.aidlStatus(isConnected)
    }

    fun stringChanged(string: String) {
        aidlService?.stringChanged(string)
    }

    fun itemChanged(item: ItemBean) {
        aidlService?.itemChanged(item)
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