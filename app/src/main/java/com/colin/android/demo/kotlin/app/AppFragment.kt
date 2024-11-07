package com.colin.android.demo.kotlin.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.colin.android.demo.kotlin.receiver.ScreenChangedReceiver
import com.colin.library.android.base.BaseFragment
import com.colin.library.android.utils.L
import java.lang.reflect.ParameterizedType


abstract class AppFragment<VB : ViewBinding, VM : ViewModel> : BaseFragment(),
    ScreenChangedReceiver.OnScreenChangedListener {
    private var _viewBinding: VB? = null
    internal val viewBinding: VB get() = _viewBinding!!
    internal abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenChangedReceiver.bind(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _viewBinding = reflectViewBinding(inflater, container)
        return viewBinding.root
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }

    override fun loadData(refresh: Boolean) {
    }

    override fun screenChanged(action: String) {
        L.i(TAG, "screenChanged:$action")
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class)
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

    /**
     * add an observer within the [ViewLifecycleOwner] lifespan
     */
    inline fun <reified OUT : Any> LiveData<out OUT?>.observe(crossinline observer: (OUT) -> Unit) {
        observe(viewLifecycleOwner) { it?.let(observer) }
    }
}
