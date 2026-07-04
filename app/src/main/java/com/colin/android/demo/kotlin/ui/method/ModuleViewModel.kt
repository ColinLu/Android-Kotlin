package com.colin.android.demo.kotlin.ui.method

import androidx.annotation.ArrayRes
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.library.android.utils.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :TODO
 */
class ModuleViewModel : AppViewModel() {
    private var _list = MutableSharedFlow<List<String>>()

    val list = _list.asSharedFlow()

    suspend fun loadData(@ArrayRes arrays: Int) {
        val list = App.getInstance().resources.getStringArray(arrays).asList()
        Log.i("arrays:$arrays size:${list.size}")
        _list.emit(list)
    }
}