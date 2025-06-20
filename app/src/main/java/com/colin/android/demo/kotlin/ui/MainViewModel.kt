package com.colin.android.demo.kotlin.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _status = MutableLiveData(false)
    val status: LiveData<Boolean> = _status

    fun update(status: Boolean) {
        viewModelScope.launch { _status.value = status }
    }
}