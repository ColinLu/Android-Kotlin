package com.colin.android.demo.kotlin.ui

import android.util.SparseBooleanArray
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.launch

open class MainViewModel : AppViewModel() {
    private val _menuState = MutableLiveData(SparseBooleanArray(2))
    val menuState: LiveData<SparseBooleanArray> = _menuState

    fun updateMenu(@IdRes id: Int, value: Boolean) {
        viewModelScope.launch {
            menuState.value?.let {
                val array = it
                if (array[id] != value) {
                    array.put(id, value)
                    _menuState.value = array
                }
            } ?: run {
                val array = SparseBooleanArray(2)
                array.put(id, value)
                _menuState.value = array
            }
        }
    }
}