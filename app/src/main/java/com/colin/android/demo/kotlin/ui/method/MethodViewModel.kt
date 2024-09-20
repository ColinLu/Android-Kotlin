package com.colin.android.demo.kotlin.ui.method

import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MethodViewModel : AppViewModel() {

    private var _list = MutableStateFlow<List<String>>(emptyList())

    val list = _list.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _list.tryEmit(App.getInstance().resources.getStringArray(R.array.method_list).asList())
        }
    }
}