package com.colin.android.demo.kotlin.ui.method

import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MethodViewModel : AppViewModel() {

    private var _list = MutableSharedFlow<List<String>>()

    val list = _list.asSharedFlow()

    suspend fun loadData() {
        _list.emit(App.getInstance().resources.getStringArray(R.array.method_list).asList())

    }
}