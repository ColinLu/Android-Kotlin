package com.colin.library.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import com.colin.library.android.utils.helper.UtilHelper
import java.io.File

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-06-29 20:13
 *
 * Des   :PathUtil
 */
object PathUtil {
    private const val DB_FILE = "databases"
    private const val SP_FILE = "shared_prefs"
    private const val CODE_FILE = "code_cache"

    /*判断是否存在外部存储卡*/
    fun hasSDCard() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    /*外部存储是否有写权限*/
    fun canWrite(): Boolean {
        return hasSDCard() && Environment.getExternalStorageDirectory().canWrite()
    }

    /*  /system */
    fun getRootSystem() = Environment.getRootDirectory()

    /*  /data */
    fun getRootData() = Environment.getDataDirectory()

    /*/data/user/0/package*/
    @SuppressLint("ObsoleteSdkInt")
    fun getInternalData(context: Context = UtilHelper.getApplication()): File? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.dataDir else File(context.applicationInfo.dataDir)
    }

    /*/data/user/0/package/files*/
    fun getInternalFiles(context: Context = UtilHelper.getApplication()) = context.filesDir

    /*/data/data/package/cache */
    fun getInternalCache(context: Context = UtilHelper.getApplication()) = context.cacheDir

    /*/data/user/0/package/code_cache*/
    fun getInternalCode(context: Context = UtilHelper.getApplication()) =
        File(getInternalPath(context, CODE_FILE))

    /*/data/user/0/package/databases*/
    fun getInternalDatabase(context: Context = UtilHelper.getApplication()) =
        File(getInternalPath(context, DB_FILE))

    /*/data/user/0/package/shared_prefs*/
    fun getInternalSp(context: Context = UtilHelper.getApplication()) =
        File(getInternalPath(context, SP_FILE))


    /*  storage/emulated/0*/
    fun getExternalStorage(): File? {
        return Environment.getExternalStoragePublicDirectory(null)
    }

    /**
     * /storage/emulated/0/Android/data/package/files/type
     * type==null-->>/storage/emulated/0/Android/data/package/files
     * type==xxx -->>/storage/emulated/0/Android/data/package/files/xxx
     * @param context
     * @param type
     * @return
     */
    fun getAppExternalFile(context: Context = UtilHelper.getApplication(), type: String?) =
        context.getExternalFilesDir(type)

    /*/storage/emulated/0/Android/data/package/cache*/
    fun getExternalCache(context: Context = UtilHelper.getApplication()) = context.externalCacheDir


    /*  /data/cache*/
    fun getDownloadCache() = Environment.getDownloadCacheDirectory()

    /**
     * /storage/emulated/0/type
     * type==null-->>/storage/emulated/0
     * type==xxx -->>/storage/emulated/0/xxx
     * @param type #
     * @return
     */
    fun getStorageExternalFile(type: String): File? {
        return Environment.getExternalStoragePublicDirectory(type)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getInternalPath(context: Context = UtilHelper.getApplication()): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            context.applicationInfo.dataDir
        } else context.dataDir.absolutePath
    }

    fun getInternalPath(context: Context, name: String) =
        "${getInternalPath(context)}${File.separator}$name"

    fun getPath(file: File?): String? {
        return file?.path
    }

    fun getAbsolutePath(file: File?): String? {
        return file?.absolutePath
    }

}