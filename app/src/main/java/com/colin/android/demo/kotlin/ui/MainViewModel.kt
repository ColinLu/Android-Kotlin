package com.colin.android.demo.kotlin.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.launch

open class MainViewModel : AppViewModel() {
    private val _search = MutableLiveData(false)
    val search: LiveData<Boolean> = _search

    fun updateSearch(status: Boolean) {
        viewModelScope.launch { _search.value = status }
    }
}