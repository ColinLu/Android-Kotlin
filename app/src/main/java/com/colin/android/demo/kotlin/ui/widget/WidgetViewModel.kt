package com.colin.android.demo.kotlin.ui.widget

import androidx.lifecycle.viewModelScope
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.App
import com.colin.android.demo.kotlin.app.AppViewModel
import com.colin.android.demo.kotlin.app.DELAY_TIME_REFRESH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class WidgetViewModel : AppViewModel() {

    private var _list = MutableSharedFlow<List<String>>()
    val list = _list.asSharedFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            loading(true)
            delay(DELAY_TIME_REFRESH.milliseconds)
            _list.emit(App.getInstance().resources.getStringArray(R.array.widget_list).asList())
            loading(false)
        }
    }


}
