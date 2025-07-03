package com.colin.library.android.utils

import java.io.BufferedReader
import java.io.Closeable
import java.io.Flushable
import java.io.IOException
import java.io.InputStreamReader

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 21:20
 *
 * Des   :IOUtil
 */
object IOUtil {

    fun read(read: InputStreamReader): String {
        return BufferedReader(read).use { it.readLines().joinToString(LINE_SEP) }
    }

    /**
     * 关闭数据流
     *
     * @param closeables closeables
     */
    fun close(vararg closeables: Closeable?) {
        if (closeables.isEmpty()) return
        for (closeable in closeables) {
            if (null == closeable) continue
            try {
                closeable.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * flush() 是清空，而不是刷新啊。
     * 一般主要用在IO中，即清空缓冲区数据，就是说你用读写流的时候，其实数据是先被读到了内存中，
     * 然后用数据写到文件中，当你数据读完的时候不代表你的数据已经写完了，因为还有一部分有可能会留在内存这个缓冲区中。
     * 这时候如果你调用了 close()方法关闭了读写流，那么这部分数据就会丢失，所以应该在关闭读写流之前先flush()，先清空数据。
     *
     *
     * 需要将FileOutputStream作为BufferedOutputStream构造函数的参数传入，
     * 然后对BufferedOutputStream进行写入操作，才能利用缓冲及flush()。
     *
     * @param flushable
     */
    fun flush(vararg flushable: Flushable?) {
        if (flushable.isEmpty()) return
        for (flush in flushable) {
            if (null == flush) continue
            try {
                flush.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}