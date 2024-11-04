package com.colin.android.demo.kotlin.app

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.viewbinding.ViewBinding
import com.colin.library.android.base.BaseDialog
import com.colin.library.android.utils.L
import java.lang.reflect.ParameterizedType


open class AppDialog<VB : ViewBinding> : BaseDialog() {
    private var _viewBinding: VB? = null
    val viewBinding: VB get() = _viewBinding!!

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _viewBinding = reflectViewBinding(inflater, container)
        return viewBinding.root
    }

    override fun initWindow(window: Window) {
        TODO("Not yet implemented")
    }

    override fun createDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun initData(bundle: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun loadData(refresh: Boolean) {
        TODO("Not yet implemented")
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
            L.log(e)
        }
        throw IllegalArgumentException("ViewBinding.inflate(inflater, container, false) error:$this")
    }
}
