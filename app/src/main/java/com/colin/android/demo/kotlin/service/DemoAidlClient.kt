package com.colin.android.demo.kotlin.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.colin.android.demo.kotlin.IDemoAidlCallback
import com.colin.android.demo.kotlin.IDemoAidlInterface
import com.colin.android.demo.kotlin.def.ItemBean
import com.colin.library.android.utils.Log

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-10-18 08:55
 *
 * Des   :DemoAIDLClient
 */
class DemoAidlClient(val context: Context) {
    companion object {
        private const val AIDL_ACTION = "com.colin.android.demo.kotlin.service.DemoAidlService"
        private const val AIDL_PACKAGE = "com.colin.android.demo.kotlin"
    }

    private var aidlService: IDemoAidlInterface? = null
    private val callback = object : IDemoAidlCallback.Stub() {
        override fun aidlStatus(isConnected: Boolean) {
            Log.i("aidlStatus:$isConnected")
        }

        override fun aidlChanged(data: String?) {
            Log.i("aidlChanged:$data")
        }

        override fun itemChanged(itembean: ItemBean?) {
            Log.i("itemChanged:$itembean")
        }

    }


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            aidlService = IDemoAidlInterface.Stub.asInterface(service)
            aidlService?.register(callback)
            aidlService?.aidlStatus(true)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService?.aidlStatus(false)
            aidlService?.unregister(callback)
            aidlService = null
        }
    }

    fun bindService() {
        val intent = Intent(AIDL_ACTION).apply {
            `package` = AIDL_PACKAGE
        }
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        context.unbindService(connection)
    }
}