package com.colin.android.demo.kotlin.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.colin.android.demo.kotlin.app.AppViewModel

class SlideshowViewModel : AppViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text
}