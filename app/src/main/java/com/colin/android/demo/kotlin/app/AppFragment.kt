package com.colin.android.demo.kotlin.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.colin.library.android.base.BaseFragment
import java.lang.reflect.ParameterizedType


abstract class AppFragment<VB : ViewBinding, VM : ViewModel> : BaseFragment() {
    private var _viewBinding: VB? = null
    val viewBinding: VB by lazy { _viewBinding!! }
    abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _viewBinding = reflectViewBinding(inflater, container)
        return _viewBinding!!.root
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    override fun loadData(refresh: Boolean) {
    }

    @Suppress("UNCHECKED_CAST")
    private fun <VB : ViewBinding> reflectViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): VB {
        val type = javaClass.genericSuperclass as ParameterizedType
        val cls = type.actualTypeArguments[0] as Class<*>
        try {
            val inflate = cls.getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            return inflate.invoke(null, inflater, container, false) as VB
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalArgumentException("ViewBinding.inflate(inflater, container, false) error")
    }
}
