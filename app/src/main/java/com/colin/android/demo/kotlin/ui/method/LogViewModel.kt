package com.colin.android.demo.kotlin.ui.method

import com.colin.android.demo.kotlin.R
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
class LogViewModel : AppViewModel() {
    private var _list = MutableSharedFlow<List<String>>()

    val list = _list.asSharedFlow()

    suspend fun loadData() {
        val list = App.getInstance().resources.getStringArray(R.array.log_list).asList()
        Log.i("LogViewModel", "$list")
        _list.emit(list)
    }
}