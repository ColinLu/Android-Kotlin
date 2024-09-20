package com.colin.android.demo.kotlin.ui.home

import android.os.Bundle
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.FragmentHomeBinding
import com.colin.library.android.utils.L


class HomeFragment : AppFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by lazy { createModel(this) }

    override fun initView(savedInstanceState: Bundle?) {
        viewBinding.buttonPicture.setOnClickListener {

        }
    }

    override fun initData(bundle: Bundle?) {
        viewModel.text.observe(this) {
            L.i(it)
            L.i(it)
            viewBinding.text.text = it
        }
//        requestMultiplePermissions.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        )
    }


//     Request permission contract
//    private val requestPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            // Do something if permission granted
////            if (isGranted) toast("Permission is granted")
////            else toast("Permission is denied")
//        }

    // Request multiple permissions contract
//    private val requestMultiplePermissions =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
//            // Do something if some permissions granted or denied
//            result.entries.forEach { (k, v) -> L.i("permission:$k,isGranted:$v") }
//        }
}