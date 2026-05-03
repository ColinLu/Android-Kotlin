package com.colin.library.android.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-10-08 23:22
 *
 * Des   :FlowExt
 */
/**
 * 在指定的生命周期状态下收集Flow数据
 *
 * @param owner 生命周期所有者
 * @param state 生命周期状态，默认为STARTED
 * @param collector 数据收集器
 */
fun <T> Flow<T>.collectOnLife(
    owner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: (T) -> Unit
) {
    owner.lifecycleScope.launch {
//        owner.repeatOnLifecycle(state) {
//            collect(collector)
//        }
    }
}