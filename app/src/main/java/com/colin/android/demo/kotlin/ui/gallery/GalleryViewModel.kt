package com.colin.android.demo.kotlin.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.colin.android.demo.kotlin.app.AppViewModel

class GalleryViewModel : AppViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
}