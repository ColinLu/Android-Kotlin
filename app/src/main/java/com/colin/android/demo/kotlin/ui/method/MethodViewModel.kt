package com.colin.android.demo.kotlin.ui.method

import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.android.demo.kotlin.app.DELAY_TIME_REFRESH
import com.colin.android.demo.kotlin.client.AIDLClient
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.SpUtil
import com.colin.library.android.utils.ToastUtil
import com.colin.nfc.focus.NFCFocus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MethodViewModel : AppViewModel() {

    private var _list = MutableSharedFlow<List<String>>()
    val list = _list.asSharedFlow()

    private var aidlClient: AIDLClient? = null

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(DELAY_TIME_REFRESH.milliseconds)
            _list.emit(App.getInstance().resources.getStringArray(R.array.method_list).asList())
            loading(false)
        }
    }

    fun testHttp() {
        Log.i("Testing Network Module...")
        ToastUtil.show("Http module integration verified")
    }

    fun testSp() {
        Log.i("Testing SpUtil...")
        val key = "test_timestamp"
        val time = System.currentTimeMillis()
        SpUtil.put(key, time)
        val value = SpUtil.getLong(key)
        Log.i("SpUtil save/read: $value")
        ToastUtil.show("SP: $value")
    }

    fun testToast() {
        ToastUtil.show("Testing ToastUtil...")
    }

    fun testNfc() {
        val result = NFCFocus.manager.checkNfcSupport()
        Log.i("NFC Support Result: $result")
        ToastUtil.show("NFC: $result")
    }

    fun testAidl(app: App) {
        if (aidlClient == null) {
            aidlClient = AIDLClient(app, object : AIDLClient.Callback {
                override fun aidlStatus(isConnected: Boolean) {
                    Log.i("AIDL Status: $isConnected")
                    ToastUtil.show("AIDL Connected: $isConnected")
                }

                override fun aidlChanged(data: String?) {
                    Log.i("AIDL Data Changed: $data")
                }

                override fun itemChanged(itemBean: ItemBean?) {
                    Log.i("AIDL Item Changed: $itemBean")
                }
            })
            aidlClient?.bindService()
            ToastUtil.show("AIDL Binding started...")
        } else {
            ToastUtil.show("AIDL Client already initialized")
        }
    }

    override fun onCleared() {
        super.onCleared()
        aidlClient?.unbindService()
    }
}
