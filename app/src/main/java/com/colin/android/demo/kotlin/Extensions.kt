package com.colin.android.demo.kotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified VM : ViewModel> createModel(owner: ViewModelStoreOwner): VM {
    return ViewModelProvider(owner)[VM::class.java]
}





