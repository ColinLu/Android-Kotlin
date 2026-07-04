package com.colin.android.demo.kotlin.ui.method.aidl

import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AidlViewModel : AppViewModel() {
    private var _registerStatus = MutableStateFlow<Boolean>(false)
    val registerStatus = _registerStatus.asStateFlow()

    fun updateStatus(status: Boolean) {
        viewModelScope.launch { _registerStatus.emit(status) }
    }
}
