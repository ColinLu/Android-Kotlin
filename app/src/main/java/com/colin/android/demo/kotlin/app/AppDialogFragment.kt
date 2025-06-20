package com.colin.android.demo.kotlin.app

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.colin.library.android.base.BaseDialogFragment
import com.colin.library.android.utils.Log
import java.lang.reflect.ParameterizedType


abstract class AppDialogFragment<VB : ViewBinding>(builder: Builder<*, *>? = null) :
    BaseDialogFragment(builder?.layoutRes ?: Resources.ID_NULL, builder) {

    private var _viewBinding: VB? = null
    internal val viewBinding: VB get() = _viewBinding!!

    override fun createView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _viewBinding = reflectViewBinding(inflater, container)
        return viewBinding.root
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
            Log.log(e)
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
