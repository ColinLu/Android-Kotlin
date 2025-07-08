package com.colin.android.demo.kotlin.ui

import android.util.SparseBooleanArray
import androidx.annotation.IdRes
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MainViewModel : AppViewModel() {
    private val _menuState = MutableStateFlow(SparseBooleanArray(2))
    val menuState = _menuState.asStateFlow()
    fun updateMenu(@IdRes id: Int, value: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val array = menuState.value
            if (array[id] != value) {
                array.put(id, value)
                _menuState.emit(array)
            }
        }
    }
}