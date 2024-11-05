package com.colin.android.demo.kotlin.app

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.colin.library.android.base.BaseActivity
import com.colin.library.android.utils.L
import java.lang.reflect.ParameterizedType


abstract class AppActivity<VB : ViewBinding, VM : ViewModel> : BaseActivity() {
    internal lateinit var viewBinding: VB
        private set
    internal lateinit var viewModel: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = reflectViewBinding()
        setContentView(viewBinding.root, savedInstanceState)
    }

    override fun loadData(refresh: Boolean) {

    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class)
    private fun <VB : ViewBinding> reflectViewBinding(): VB {
        try {
            val type = javaClass.genericSuperclass as ParameterizedType
            val cls = type.actualTypeArguments[0] as Class<*>
            val inflate = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
            return inflate.invoke(null, layoutInflater) as VB
        } catch (e: Exception) {
            L.log(e)

        }
        throw IllegalStateException("reflectViewBinding fail in $TAG")
    }
}



