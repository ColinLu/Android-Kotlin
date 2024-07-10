package com.colin.android.demo.kotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

//inline fun <reified VB : ViewBinding> createBinding(inflater: LayoutInflater): VB {
//    return ActivityMainBinding.inflate(inflater)
//}

inline fun <reified VM : ViewModel> createModel(owner: ViewModelStoreOwner): VM {
    return ViewModelProvider(owner)[VM::class.java]
}

//inline fun <reified T : Any?> LiveData<out T>.observe(
//    owner: LifecycleOwner, crossinline observer: (T) -> Unit
//) {
//    observe(owner) { observer(it) }
//}

