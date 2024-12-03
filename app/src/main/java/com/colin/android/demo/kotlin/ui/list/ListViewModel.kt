package com.colin.android.demo.kotlin.ui.list

import androidx.annotation.ArrayRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.def.ItemBean
import com.colin.library.android.utils.helper.UtilHelper
import com.colin.library.android.widgets.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {
    @ArrayRes
    var id = R.array.path_list

    private val _refresh = MutableLiveData(false)
    val refresh = _refresh

    private val _list = MutableLiveData<List<ItemBean>>().apply {
        value = emptyList()
    }
    val list: LiveData<List<ItemBean>> = _list

    fun loadData() {
        val newData = ItemBean.initList(id)
        _list.postValue(newData)
    }

    fun setId(@ArrayRes id: Int) {
        this.id = id
    }

    fun loadStatus(refresh: Boolean) {
        _refresh.postValue(refresh)
    }
}