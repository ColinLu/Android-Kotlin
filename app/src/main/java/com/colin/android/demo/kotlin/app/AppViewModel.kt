package com.colin.android.demo.kotlin.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
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
open class AppViewModel : ViewModel() {

    /*加载状态*/
    private val _showLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /*加载状态*/
    val showLoading: Flow<Boolean> = _showLoading.asStateFlow()

    fun launch(showLoading: Boolean = true, block: suspend () -> Unit) =
        launch(showLoading, block) {
            action(it)
        }

    fun launch(
        showLoading: Boolean, block: suspend () -> Unit, error: suspend (Throwable) -> Unit
    ) = viewModelScope.launch {
        launch(showLoading, false, block, error)
    }

    fun launch(
        showLoading: Boolean,
        interceptError: Boolean,
        block: suspend () -> Unit,
        error: suspend (Throwable) -> Unit
    ) = viewModelScope.launch {
        try {
            if (showLoading) _showLoading.tryEmit(true)
            block()
        } catch (e: Throwable) {
            error(e)
            if (interceptError) action(e)
        }
        if (showLoading) _showLoading.tryEmit(false)
    }

    /**
     * 公共异常消息处理
     */
    open fun action(it: Throwable) {

    }
}