package com.colin.library.android.utils

import android.Manifest.permission
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import androidx.annotation.IntDef
import androidx.annotation.RequiresPermission
import com.colin.library.android.utils.helper.UtilHelper
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.LinkedList
import java.util.Locale

/**
 * 作者： ColinLu
 * 时间： 2018-12-13 15:39
 *
 *
 * 描述： 网络工具类
 */
@IntDef(
    NetType.NETWORK_NONE,
    NetType.NETWORK_MOBILE,
    NetType.NETWORK_WIFI,
    NetType.NETWORK_2G,
    NetType.NETWORK_3G,
    NetType.NETWORK_4G,
    NetType.NETWORK_5G,
    NetType.NETWORK_ETHERNET
)
@Retention(
    AnnotationRetention.SOURCE
)
annotation class NetType {
    companion object {
        const val NETWORK_NONE: Int = -1    // 没有网络连接
        const val NETWORK_MOBILE: Int = 0   // 手机流量
        const val NETWORK_WIFI: Int = 1     // wifi连接
        const val NETWORK_2G: Int = 2       // 2G
        const val NETWORK_3G: Int = 3       // 3G
        const val NETWORK_4G: Int = 4       // 4G
        const val NETWORK_5G: Int = 5       // 5G
        const val NETWORK_ETHERNET: Int = 6 // 以太网
    }
}

object NetUtil {
    val manager: ConnectivityManager
        get() {
            return UtilHelper.getApplication().getSystemService(ConnectivityManager::class.java)
        }
    private val telephonyManager: TelephonyManager
        get() {
            return UtilHelper.getApplication().getSystemService(TelephonyManager::class.java)
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val activeNetworkInfo: NetworkInfo?
        get() {
            return manager.activeNetworkInfo
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isConnected: Boolean
        /**
         * 判断网络是否连接
         *
         * @return
         */
        get() {
            val networkInfo = activeNetworkInfo
            return null != networkInfo && networkInfo.isConnected
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isWifi: Boolean
        /**
         * 判断是否是wifi连接
         */
        get() = isWifi(activeNetworkInfo)

    fun isWifi(networkInfo: NetworkInfo?): Boolean {
        return null != networkInfo && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private val isEthernet: Boolean
        get() = isEthernet(manager)

    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    private fun isEthernet(manager: ConnectivityManager): Boolean {
        val networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
        return null != networkInfo && networkInfo.isConnected
    }

    /**
     * 打开或关闭移动数据网
     *
     * @param enabled `true`: 打开
     * `false`: 关闭
     */
    fun setDataEnabled(enabled: Boolean) {
        val manager: TelephonyManager =
            UtilHelper.getSystemService(TelephonyManager::class.java) ?: return
        try {
            val setMobileDataEnabledMethod = manager.javaClass.getDeclaredMethod(
                "setDataEnabled", Boolean::class.javaPrimitiveType
            )
            setMobileDataEnabledMethod.invoke(manager, enabled)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    val isNetworkAvailable: Boolean
        /**
         * 检查当前网络是否可用
         *
         * @return
         */
        get() {
            // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
            val networkInfo = manager.allNetworkInfo
            if (networkInfo.isEmpty()) return false
            for (i in networkInfo.indices) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].state == NetworkInfo.State.CONNECTED) return true
            }
            return false
        }

    @get:RequiresPermission(permission.ACCESS_NETWORK_STATE)
    @get:NetType
    val netType: Int
        get() = getNetType(manager)

    @NetType
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun getNetType(manager: ConnectivityManager): Int {
        if (isEthernet(manager)) return NetType.NETWORK_ETHERNET
        return getNetType(activeNetworkInfo)
    }

    @NetType
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun getNetType(networkInfo: NetworkInfo?): Int {
        //获取失败
        if (null == networkInfo || !networkInfo.isAvailable) return NetType.NETWORK_NONE
        //WIFI
        if (networkInfo.type == ConnectivityManager.TYPE_WIFI) return NetType.NETWORK_WIFI
        return when (networkInfo.subtype) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM -> NetType.NETWORK_2G

            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA, 19 -> NetType.NETWORK_3G

            TelephonyManager.NETWORK_TYPE_LTE,
            TelephonyManager.NETWORK_TYPE_IWLAN -> NetType.NETWORK_4G

            TelephonyManager.NETWORK_TYPE_NR -> NetType.NETWORK_5G
            else -> getNetType(networkInfo.subtypeName)
        }
    }

    @NetType
    private fun getNetType(subType: String?): Int {
        if ("TD-SCDMA".equals(subType, ignoreCase = true)
            || "WCDMA".equals(subType, ignoreCase = true)
            || "CDMA2000".equals(subType, ignoreCase = true)
        ) return NetType.NETWORK_3G
        return NetType.NETWORK_MOBILE
    }

    fun getNetType(@NetType type: Int): String {
        return when (type) {
            NetType.NETWORK_NONE -> "无网络"
            NetType.NETWORK_WIFI -> "WIFI"
            NetType.NETWORK_ETHERNET -> "以太网"
            NetType.NETWORK_2G -> "2G"
            NetType.NETWORK_3G -> "3G"
            NetType.NETWORK_4G -> "4G"
            NetType.NETWORK_5G -> "5G"
            NetType.NETWORK_MOBILE -> "未知网络"
            else -> "未知网络"
        }
    }

    val networkOperatorName: String?
        /**
         * 获取网络运营商名称 英文
         * China Telecom
         *
         * @return 运营商名称
         */
        get() {
            val manager = telephonyManager
            if (manager.simState == TelephonyManager.SIM_STATE_READY) return manager.networkOperatorName
            return null
        }

    val simOperatorName: String?
        /**
         * 获取电话卡运营商名字 中文 中国移动、如中国联通、中国电信
         *
         * @return
         */
        get() {
            val manager = telephonyManager
            if (manager.simState == TelephonyManager.SIM_STATE_READY) return manager.simOperatorName
            return null
        }


    /**
     * 获取IP地址
     *
     *
     * 需添加权限
     * `<uses-permission android:name="android.permission.INTERNET"/>`
     *
     *
     * @param useIPv4 是否用IPv4
     * @return IP地址
     */
    @RequiresPermission(permission.INTERNET)
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress
                    val isIPv4 = (hostAddress?.indexOf(':') ?: 0) < 0
                    if (useIPv4) if (isIPv4) return hostAddress
                    else {
                        val index = hostAddress?.indexOf('%') ?: 0
                        return if (index < 0) hostAddress.uppercase(Locale.getDefault())
                        else hostAddress?.substring(0, index)?.uppercase(Locale.getDefault())!!
                    }
                }
            }
        } catch (e: SocketException) {
            L.log(e)
        }
        return ""
    }

    val broadcastIpAddress: String
        /**
         * Return the ip address of broadcast.
         *
         * @return the ip address of broadcast
         */
        get() {
            try {
                val nis = NetworkInterface.getNetworkInterfaces()
                val adds = LinkedList<InetAddress>()
                while (nis.hasMoreElements()) {
                    val ni = nis.nextElement()
                    if (!ni.isUp || ni.isLoopback) continue
                    val ias = ni.interfaceAddresses
                    var i = 0
                    val size = ias.size
                    while (i < size) {
                        val ia = ias[i]
                        val broadcast = ia.broadcast
                        if (broadcast != null) return broadcast.hostAddress
                        i++
                    }
                }
            } catch (e: SocketException) {
                L.log(e)
            }
            return ""
        }
}