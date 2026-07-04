package com.colin.android.demo.kotlin.ui.list

import android.content.res.Resources
import androidx.annotation.ArrayRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.colin.android.demo.kotlin.ItemBean
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.library.android.utils.helper.UtilHelper

class ListViewModel : AppViewModel() {
    @ArrayRes
    var id = R.array.path_list
        set(value) {
            if (field != value && value != Resources.ID_NULL) field = value
        }

    private val _refresh = MutableLiveData(false)
    val refresh = _refresh

    private val _list = MutableLiveData<List<ItemBean>>().apply {
        value = emptyList()
    }
    val list: LiveData<List<ItemBean>> = _list

    fun loadData() {
        val array = UtilHelper.getApplication().resources.getStringArray(id)
        val newData = array.mapIndexed { index, s -> ItemBean(index, s) }
        _list.postValue(newData)
    }

    fun loadStatus(refresh: Boolean) {
        _refresh.postValue(refresh)
    }
}