package com.colin.android.demo.kotlin.ui.widget.web

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.library.android.utils.SpUtil
import com.colin.library.android.utils.helper.UtilHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 13:20
 *
 * Des   :WebViewModel
 */
class WebViewModel : AppViewModel() {
    private val _history = MutableLiveData<List<String>>()
    var history: LiveData<List<String>> = _history

    companion object {
        const val HISTORY_KEY = "history"
    }

    fun loadData(refresh: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(3000)
            var urls = UtilHelper.getApplication().resources.getStringArray(R.array.url_address)
                .toMutableSet()
            val local = SpUtil.getString(HISTORY_KEY)?.split(",")
                ?.filter { it.isEmpty().not() && !urls.contains(it) }
            if (local.isNullOrEmpty().not()) urls.addAll(local)
            _history.postValue(urls.toList())
            loading(false)
        }
    }

    fun record(url: String) {
        if (history.value?.contains(url) == true) return
        SpUtil.getString(HISTORY_KEY)?.let {
            SpUtil.put(HISTORY_KEY, "$it,$url")
        }
    }


}