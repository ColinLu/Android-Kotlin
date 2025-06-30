package com.colin.android.demo.kotlin.ui.list

import androidx.annotation.ArrayRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.android.demo.kotlin.def.ItemBean
import com.colin.library.android.utils.INVALID
import com.colin.library.android.utils.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListViewModel : AppViewModel() {

    private val _list = MutableLiveData<List<ItemBean>>().apply {
        value = emptyList()
    }

    val list: LiveData<List<ItemBean>> = _list

    fun loadData(@ArrayRes id: Int = R.array.flow_data) {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(3000)
            if (id != INVALID) {
                _list.postValue(ItemBean.initList(id))
            } else {
                Log.e("ArrayRes:$id is error")
            }
            loading()
        }
    }
}