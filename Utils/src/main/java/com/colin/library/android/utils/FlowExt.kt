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
fun <T> Flow<T>.collectOnLife(
    owner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: (T) -> Unit
) {
    owner.lifecycleScope.launch {
        //repeatOnLifecycle(state) { collect(collector) }
    }
}