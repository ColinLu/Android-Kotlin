package com.colin.android.demo.kotlin.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.colin.android.demo.kotlin.app.AppViewModel

class HomeViewModel : AppViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}