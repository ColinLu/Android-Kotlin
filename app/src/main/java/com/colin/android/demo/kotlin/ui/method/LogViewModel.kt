package com.colin.android.demo.kotlin.ui.method

import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :TODO
 */
class LogViewModel : AppViewModel() {
    private var _list = MutableStateFlow<List<String>>(emptyList())

    val list = _list.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _list.tryEmit(App.getInstance().resources.getStringArray(R.array.log_list).asList())
        }
    }
}