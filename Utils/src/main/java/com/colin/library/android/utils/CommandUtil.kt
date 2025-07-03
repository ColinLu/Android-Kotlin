package com.colin.library.android.utils

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-03 11:03
 *
 * Des   :CommandUtil
 */
object CommandUtil {
    const val COMMAND_SU: String = "su"
    const val COMMAND_SH: String = "sh"
    const val COMMAND_EXIT: String = "exit"

    /*wifi mac address*/
    const val COMMAND_MAC_ADDRESS: String = " cat /sys/class/net/wlan0/address"
    const val COMMAND_ROOT_PERMISSION: String = "echo root"


    fun getMacAddress(): String? {
        val result = execCmd(false, COMMAND_MAC_ADDRESS)
        return result.success
    }

    /*判断 APP 是否授权 Root 权限*/
    fun isGrantedRoot(): Boolean {
        return execCmd(true, COMMAND_ROOT_PERMISSION).code == 0
    }

    @Synchronized
    fun execCmd(command: String, size: Int = 1024): String? {
        var process: Process? = null
        var read: BufferedReader? = null
        try {
            process = Runtime.getRuntime().exec(command)
            read = BufferedReader(InputStreamReader(process.inputStream, Charsets.UTF_8), size)
            return read.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            IOUtil.close(read)
            process?.destroy()
        }
        return null
    }

    /**
     * 同步执行多条命令行
     *
     * @param commands    命令行
     * @param root        是否以Root 权限执行
     * @return 返回结果
     */
    @Synchronized
    fun execCmd(
        root: Boolean = false, vararg commands: String
    ): Result {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            var result = INVALID
            var success: String? = null
            var error: String? = null
            process = Runtime.getRuntime().exec(if (root) COMMAND_SU else COMMAND_SH)
            os = DataOutputStream(process.outputStream)
            for (command in commands) {
                os.write(command.toByteArray())
                os.writeBytes(LINE_SEP)
            }
            os.writeBytes(COMMAND_EXIT + LINE_SEP)
            os.flush()
            success = IOUtil.read(InputStreamReader(process.inputStream, Charsets.UTF_8))
            error = IOUtil.read(InputStreamReader(process.errorStream, Charsets.UTF_8))
            result = process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result(INVALID, null, Log.getStackTraceString(e))
        } finally {
            IOUtil.close(os)
            process?.destroy()
        }
        return Result(INVALID, null, "error")
    }

    data class Result(val code: Int, val success: String?, val failure: String?)
}