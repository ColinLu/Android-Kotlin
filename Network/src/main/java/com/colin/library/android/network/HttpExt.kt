package com.colin.library.android.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colin.library.android.network.data.ApiException
import com.colin.library.android.network.data.AppResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketException

/**
 * 核心实现：通过ViewMode异步发起网络请求，支持延迟启动和自动重试机制。
 *
 * @param request 网络请求体。
 * @param result 成功时回调，接收响应数据 [T]。
 * @param state 状态变化回调，适用于各种业务状态反馈（包括成功、失败等）。
 * @param loading 用于网络请求状态弹框展示。
 * @param retry 最大重试次数。
 * @param delay 请求前延迟时间（毫秒）。
 * @return 返回 [Job]，可用于取消请求。
 */
fun <T> ViewModel.request(
    request: suspend () -> AppResponse<T>,
    result: (suspend (T?) -> Unit) = { },
    state: (suspend (Int, String) -> Unit) = { _, _ -> Unit },
    loading: (suspend (Boolean) -> Unit) = {},
    retry: Int = NetworkHelper.retry,
    delay: Long = NetworkHelper.delay
) = request(viewModelScope, request, result, state, loading, retry, delay)

/**
 * 核心实现：异步发起网络请求，支持延迟启动和自动重试机制。
 *
 * @param scope 协程作用域，控制生命周期。
 * @param request 网络请求体。
 * @param result 成功时回调，接收响应数据 [T]。
 * @param state 状态变化回调，适用于各种业务状态反馈（包括成功、失败等）。
 * @param loading 用于网络请求状态弹框展示。
 * @param retry 最大重试次数。
 * @param delay 请求前延迟时间（毫秒）。
 * @return 返回 [Job]，可用于取消请求。
 */
fun <T> request(
    scope: CoroutineScope,
    request: suspend () -> AppResponse<T>,
    result: (suspend (T?) -> Unit) = { },
    state: (suspend (Int, String) -> Unit) = { _, _ -> Unit },
    loading: (suspend (Boolean) -> Unit) = { },
    retry: Int = NetworkHelper.retry,
    delay: Long = NetworkHelper.delay
): Job {
    return scope.launch(Dispatchers.IO) {
        try {
            loading.invoke(true)
            // 如果设置了延迟，则等待指定时间
            if (delay > 0L) delay(delay)
            val response = requestResult(request, retry)
            if (response.isSuccess()) result.invoke(response.getData())
            //主要用户成功也可能需要toast提示
            state.invoke(response.getCode(), response.getMsg())
            loading.invoke(false)
        } catch (e: Exception) {
            // 如果协程被取消，则直接退出
            e.printStackTrace()
            if (e is CancellationException) return@launch
            if (e is InterruptedIOException && "timeout" == e.message) return@launch
            if (e is IOException && "Canceled" == e.message) return@launch
            NetworkHelper.handleFailure(state, e)
            loading.invoke(false)
        }
    }
}

/**
 * 挂起函数版：适用于已处于协程上下文中的调用。
 *
 * @param request 网络请求体。
 * @param retry 最大重试次数。
 */
private suspend fun <T> requestResult(
    request: suspend () -> AppResponse<T>?, retry: Int = NetworkHelper.retry
): AppResponse<T> {
    var result: AppResponse<T>? = null
    var exception: Exception? = null
    // 尝试执行请求，最多重试[retry]次
    for (i in 0 until retry) {
        try {
            result = withContext(Dispatchers.IO) { withTimeout(10 * 1000) { request() } }
            break
        } catch (e: Exception) {
            exception = e
            // 仅在网络连接中断或"reset"错误时重试
            if (e is SocketException || e.message?.contains("reset") == true) delay(500L)
            else break
        }
    }
    // 如果所有重试都失败，抛出异常
    if (result == null) throw exception ?: SocketException()
    return result
}

/**
 * 通过 Flow 发起网络请求，适用于协程上下文中使用。
 *
 * @param request 网络请求体。
 * @param state 状态变化回调，适用于各种业务状态反馈（包括成功、失败等）。
 * @param loading 控制加载框显示或隐藏。
 * @return 返回解析后的数据结果 [T?]
 */
suspend fun <T> requestFlow(
    request: suspend () -> AppResponse<T>,
    state: (suspend (Int, String) -> Unit) = { _, _ -> Unit },
    loading: ((Boolean) -> Unit) = {}
): T? {
    var data: T? = null
    val flow = requestResult(request, state, loading)
    //7.调用collect获取emit()回调的结果，就是请求最后的结果
    flow.collect { data = it }
    return data
}

/**
 * 构建并返回一个 Flow，用于执行网络请求。
 *
 * 包含超时处理、异常捕获、加载状态切换等功能。
 * @param request 网络请求体。
 * @param state 状态变化回调，适用于各种业务状态反馈。
 * @param loading 控制加载框显示或隐藏。
 * @return 返回封装后的 [Flow]<[T?]>
 */
private suspend fun <T> requestResult(
    request: suspend () -> AppResponse<T>?,
    state: (suspend (Int, String) -> Unit) = { _, _ -> Unit },
    loading: ((Boolean) -> Unit) = {}
): Flow<T?> {
    val flow = flow {//1.执行请求
        //设置超时时间
        val result = withTimeout(10 * 1000) { request() }
        //2.发送网络请求结果回调
        if (result == null) throw SocketException()
        if (result.isSuccess() == true) {
            emit(result.getData())
            state.invoke(result.getCode(), result.getMsg())
        } else throw ApiException(result.getCode(), result.getMsg())
    }.flowOn(Dispatchers.IO)//3.指定运行的线程，flow {}执行的线程
        .onStart {//4.请求开始，展示加载框
            loading.invoke(true)
        }.catch { e ->//5.捕获异常
            e.printStackTrace()
            NetworkHelper.handleFailure(state, e)
        }.onCompletion { //6.请求完成，包括成功和失败
            loading.invoke(false)
        }
    return flow
}



