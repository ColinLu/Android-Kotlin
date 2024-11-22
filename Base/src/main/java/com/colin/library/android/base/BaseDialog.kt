package com.colin.library.android.base

import android.app.Dialog
import android.content.Context
import androidx.annotation.StyleRes
import androidx.annotation.UiContext

 class  BaseDialog(@UiContext context:Context , @StyleRes  themeResId:Int):
    Dialog(context,themeResId) {



}