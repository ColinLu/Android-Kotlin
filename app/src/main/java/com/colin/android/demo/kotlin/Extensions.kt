package com.colin.android.demo.kotlin

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.NavHostFragment

inline fun <reified VM : ViewModel> createModel(owner: ViewModelStoreOwner): VM {
    return ViewModelProvider(owner)[VM::class.java]
}

@MainThread
fun <T : ViewModel> createModel(owner: ViewModelStoreOwner, clazz: Class<T>): T {
    return ViewModelProvider(owner)[clazz]
}


fun toNavigate(fragment: Fragment?, action: Int) {
    toNavigate(fragment, action, null)
}

fun toNavigate(fragment: Fragment?, action: Int, bundle: Bundle?) {
    NavHostFragment.findNavController(fragment!!).navigate(action, bundle)
}



