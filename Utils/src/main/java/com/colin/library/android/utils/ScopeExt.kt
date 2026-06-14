package com.colin.library.android.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-01 08:09
 *
 * Des   :ScopeExt
 */
/**
 * 倒计时功能（LifecycleOwner版本）
 *
 * @param time 倒计时总时长（秒），默认5秒
 * @param start 倒计时开始回调
 * @param next 每次倒计时更新回调
 * @param end 倒计时结束回调
 */
fun LifecycleOwner.countDown(
    time: Int = 5,
    start: (scope: CoroutineScope) -> Unit,
    next: (time: Int) -> Unit,
    end: () -> Unit
) = countDown(lifecycleScope, time, start, next, end)

/**
 * 倒计时功能（CoroutineScope版本）
 *
 * @param scope 协程作用域
 * @param time 倒计时总时长（秒），默认5秒
 * @param start 倒计时开始回调
 * @param next 每次倒计时更新回调
 * @param finish 倒计时结束回调
 */
fun countDown(
    scope: CoroutineScope,
    time: Int = 5,
    start: (scope: CoroutineScope) -> Unit,
    next: (time: Int) -> Unit,
    finish: () -> Unit
) {
    scope.launch {
        flow {
            for (i in time downTo 0) {
                emit(i)
                if (i > 0) delay(ONE_SECOND.toLong())
            }
        }.onStart {
            start(this@launch)
        }.onCompletion { cause ->
            if (cause !is CancellationException) {
                finish()
            }
        }.catch {
            Log.e("countDown error: ${it.message}")
        }.collect {
            next(it)
        }
    }
}

/**
 * 倒计时功能（简化版LifecycleOwner）
 *
 * @param total 倒计时总时长（秒）
 * @param onNext 每次倒计时更新回调
 * @param onStart 倒计时开始回调，可选
 * @param onFinish 倒计时结束回调，可选
 * @return Job对象，可用于取消倒计时
 */
fun LifecycleOwner.countDown(
    total: Int, onNext: (Int) -> Unit, onStart: (() -> Unit) = {}, onFinish: (() -> Unit) = {}
): Job {
    return countDown(
        this.lifecycleScope, total, onNext, onStart, onFinish
    )
}

/**
 * 倒计时功能（简化版CoroutineScope）
 *
 * @param scope 协程作用域
 * @param total 倒计时总时长（秒）
 * @param onNext 每次倒计时更新回调
 * @param onStart 倒计时开始回调，可选
 * @param onFinish 倒计时结束回调，可选
 * @return Job对象，可用于取消倒计时
 */
fun countDown(
    scope: CoroutineScope,
    total: Int,
    onNext: (Int) -> Unit,
    onStart: (() -> Unit) = {},
    onFinish: (() -> Unit) = {},
): Job {
    return scope.launch {
        flow {
            for (i in total downTo 0) {
                emit(i)
                if (i > 0) delay(1000.milliseconds)
            }
        }.flowOn(Dispatchers.Main).onStart { onStart() }.onCompletion { cause ->
            if (cause !is CancellationException) {
                onFinish()
            }
        }.onEach { onNext(it) }.launchIn(this@launch)
    }
}