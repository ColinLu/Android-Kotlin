package com.colin.android.demo.kotlin

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified VM : ViewModel> createModel(owner: ViewModelStoreOwner): VM {
    return ViewModelProvider(owner)[VM::class.java]
}

@MainThread
fun <T : ViewModel> createModel(owner: ViewModelStoreOwner, clazz: Class<T>): T {
    return ViewModelProvider(owner)[clazz]
}





