package com.colin.android.demo.kotlin.app

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.base.BaseActivity
import java.lang.reflect.ParameterizedType

/**
 * 业务层 Activity 基类，自动处理 ViewBinding 与 ViewModel
 */
abstract class AppActivity<VB : ViewBinding, VM : ViewModel> : BaseActivity() {
    
    private var _viewBinding: VB? = null
    val viewBinding: VB get() = _viewBinding!!
    
    val viewModel: VM by lazy { reflectViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = reflectViewBinding()
        setContentView(viewBinding.root)
        
        initView(intent?.extras, savedInstanceState)
        initData(intent?.extras, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _viewBinding = null
    }

    @Suppress("UNCHECKED_CAST")
    private fun reflectViewBinding(): VB {
        return try {
            val type = javaClass.genericSuperclass as ParameterizedType
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getDeclaredMethod("inflate", LayoutInflater::class.java)
            method.invoke(null, layoutInflater) as VB
        } catch (e: Exception) {
            Log.e(TAG, "Reflect ViewBinding error: ${e.message}")
            throw IllegalStateException("ViewBinding inflation failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun reflectViewModel(): VM {
        return try {
            val type = javaClass.genericSuperclass as ParameterizedType
            val clazz = type.actualTypeArguments[1] as Class<VM>
            ViewModelProvider(this)[clazz]
        } catch (e: Exception) {
            Log.e(TAG, "Reflect ViewModel error: ${e.message}")
            throw IllegalStateException("ViewModel creation failed")
        }
    }

    /**
     * LiveData 扩展观察方法
     */
    inline fun <reified T : Any> LiveData<out T?>.observe(crossinline observer: (T) -> Unit) {
        observe(this@AppActivity) { it?.let(observer) }
    }
}
