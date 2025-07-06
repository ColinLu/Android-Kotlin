package com.colin.library.android.utils

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-30 10:00
 *
 * Des   :FileUtil
 */
object FileUtil {
    /*判断是否文件，且存在*/
    fun isFile(path: String?) = isFile(getFile(path))
    fun isFile(file: File?) = file?.exists() == true && file.isFile

    /*判断是否文件夹，且存在*/
    fun isDir(path: String?) = isDir(getFile(path))
    fun isDir(dir: File?) = dir?.exists() == true && dir.isDirectory

    fun getFile(path: String?) = if (!path.isNullOrBlank()) File(path) else null

    /*创建文件*/
    fun createFile(file: File?, delete: Boolean = false): Boolean {
        //文件不存在
        file ?: return false
        //删除旧文件， 文件存在并且删除失败返回 false
        if (delete && file.exists() && !file.delete()) return false
        //不需要删除旧文件，判断是否存在  并且是文件
        if (!delete && file.exists()) return file.isFile
        //判断是否存在文件夹 或者是否创建文件夹是否成功
        if (!createDir(file.parentFile)) return false
        try {
            return file.createNewFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /*创建文件夹*/
    fun createDir(dir: File?): Boolean {
        return if (dir?.exists() == true) dir.isDirectory
        else if (dir?.exists() == false) dir.mkdirs()
        else false
    }

    /*向文件中写内容 子线程*/
    fun write(file: File?, text: String?, append: Boolean = true): Boolean {
        if (text.isNullOrEmpty() || !createFile(file, !append)) return false
        var bw: BufferedWriter? = null
        try {
            bw = BufferedWriter(FileWriter(file, append))
            bw.write(text.toString())
            IOUtil.flush(bw)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            IOUtil.close(bw)
        }
    }

}