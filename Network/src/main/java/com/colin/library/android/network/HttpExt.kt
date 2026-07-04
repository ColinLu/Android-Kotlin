package com.colin.library.android.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colin.library.android.network.data.ApiException
import com.colin.library.android.network.data.AppResponse
import com.colin.library.android.utils.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.InterruptedIOException
import java.net.SocketException

/**
 * 核心实现：异步发起网络请求，支持延迟启动和自动重试机制。
 */
fun <T> request(
    scope: CoroutineScope,
    request: suspend () -> AppResponse<T>,
    result: (suspend (T?) -> Unit) = { },
    state: (suspend (Int, String) -> Unit) = { _, _ -> Unit },
    loading: (suspend (Boolean) -> Unit) = { },
    retry: Int = NetworkHelper.retry,
    delay: Long = NetworkHelper.delay,
    timeout: Long = NetworkHelper.timeout
): Job {
    return scope.launch(Dispatchers.Main) {
        try {
            loading.invoke(true)
            if (delay > 0L) delay(delay)
            
            val response = withContext(Dispatchers.IO) {
                requestResult(request, retry, timeout)
            }
            
            if (response.isSuccess()) {
                result.invoke(response.getData())
            }
            state.invoke(response.getCode(), response.getMsg())
        } catch (e: Exception) {
            if (e is CancellationException) return@launch
            Log.e("Network", "Request failed: ${e.message}")
            NetworkHelper.handleFailure(state, e)
        } finally {
            loading.invoke(false)
        }
    }
}

/**
 * ViewModel 扩展：快速发起网络请求
 */
fun <T> ViewModel.request(
    request: suspend () -> AppResponse<T>,
    result: (suspend (T?) -> Unit) = { },
    state: (suspend (Int, String) -> Unit) = { _, _ -> Unit },
    loading: (suspend (Boolean) -> Unit) = {},
    retry: Int = NetworkHelper.retry,
    delay: Long = NetworkHelper.delay
) = request(viewModelScope, request, result, state, loading, retry, delay)

private suspend fun <T> requestResult(
    request: suspend () -> AppResponse<T>?,
    retry: Int,
    timeout: Long
): AppResponse<T> {
    var lastException: Exception? = null
    repeat(retry) {
        try {
            return withTimeout(timeout) { request() } ?: throw SocketException("Empty response")
        } catch (e: Exception) {
            lastException = e
            if (e is SocketException || e.message?.contains("reset") == true) {
                delay(500L)
            } else {
                throw e
            }
        }
    }
    throw lastException ?: SocketException("Retry failed")
}

/**
 * Flow 版本的请求实现
 */
fun <T> requestFlow(
    request: suspend () -> AppResponse<T>,
    timeout: Long = NetworkHelper.timeout
): Flow<T?> = flow {
    val response = withTimeout(timeout) { request() }
    if (response.isSuccess()) {
        emit(response.getData())
    } else {
        throw ApiException(response.getCode(), response.getMsg())
    }
}.flowOn(Dispatchers.IO)
