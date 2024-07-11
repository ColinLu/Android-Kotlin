package com.colin.android.demo.kotlin.app

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.colin.library.android.base.BaseFragment
import java.lang.reflect.ParameterizedType

abstract class AppFragment<VB : ViewBinding, VM : ViewModel> : BaseFragment() {
    private var _binding: VB? = null
    val binding: VB by lazy {
        _binding!!
    }

    abstract val model: VM

    override val layoutRes: Int = Resources.ID_NULL

    override val rootView: View
        get() = binding.root

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate(inflater, container)
        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun loadData(refresh: Boolean) {

    }

    private fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB {
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
