package com.colin.android.demo.kotlin.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.colin.android.demo.kotlin.receiver.ScreenChangedReceiver
import com.colin.library.android.base.BaseFragment
import com.colin.library.android.utils.L
import java.lang.reflect.ParameterizedType


abstract class AppFragment<VB : ViewBinding, VM : ViewModel> : BaseFragment(),
    ScreenChangedReceiver.OnScreenChangedListener {
    private var _viewBinding: VB? = null
    val viewBinding: VB get() = _viewBinding!!
    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenChangedReceiver.bind(this)
    }

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
        throw IllegalArgumentException("ViewBinding.inflate(inflater, container, false) error:$this")
    }

    override fun screenChanged(action: String) {
        L.i(TAG, "screenChanged:$action")
    }

}
