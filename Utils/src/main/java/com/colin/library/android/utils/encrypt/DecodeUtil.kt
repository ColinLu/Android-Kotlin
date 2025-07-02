package com.colin.library.android.utils.encrypt

import java.io.UnsupportedEncodingException
import java.net.URLDecoder

object DecodeUtil {
    fun url(url: String?, enc: String): String? {
        try {
            return if (url.isNullOrEmpty()) null else URLDecoder.decode(url, enc)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return url
    }
}