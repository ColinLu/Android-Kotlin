package com.colin.android.demo.kotlin.ui.method

import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MethodViewModel : AppViewModel() {

    private var _list = MutableSharedFlow<List<String>>()

    val list = _list.asSharedFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(3000)
            _list.emit(App.getInstance().resources.getStringArray(R.array.method_list).asList())
            loading()
        }
    }
}