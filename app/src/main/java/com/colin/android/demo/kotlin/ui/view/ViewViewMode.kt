package com.colin.android.demo.kotlin.ui.view

import androidx.annotation.ArrayRes
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-03 16:44
 *
 * Des   :ViewViewMode
 */
class ViewViewMode : MainViewModel() {

    private var _list = MutableSharedFlow<List<String>>()

    val list = _list.asSharedFlow()

    fun loadData(@ArrayRes id: Int = R.array.view_list) {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(3000)
            _list.emit(App.getInstance().resources.getStringArray(id).asList())
            loading()
        }
    }
}