package com.colin.nfc.focus.manager

import android.util.Log
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * 提醒频率管理器
 * 
 * 功能：
 * 1. 控制同一应用的提醒频率，避免频繁打扰
 * 2. 实现分级提醒策略（温和 → 标准 → 强制）
 * 3. 支持免打扰时段配置
 * 4. 限制每日最大提醒次数
 * 
 * 设计原则：
 * - 用户体验优先，避免过度干扰
 * - 智能递增提醒强度
 * - 可配置的频率限制
 */
object AlertFrequencyManager {

    private const val TAG = "AlertFrequencyMgr"

    // 每个应用上次提醒时间戳
    private val lastAlertTimeMap = ConcurrentHashMap<String, Long>()

    // 每个应用今日提醒次数
    private val dailyAlertCountMap = ConcurrentHashMap<String, Int>()

    // 每个应用连续提醒次数（用于分级提醒）
    private val consecutiveAlertMap = ConcurrentHashMap<String, Int>()

    // 最后重置日期
    private var lastResetDate: String = getCurrentDate()

    /**
     * 配置项
     */
    object Config {
        /**
         * 同一应用最小提醒间隔（毫秒），默认 5 分钟
         */
        var MIN_ALERT_INTERVAL_MS = 5 * 60 * 1000L

        /**
         * 每日最大提醒次数，默认 20 次
         */
        var MAX_DAILY_ALERTS = 20

        /**
         * 分级提醒阈值
         * - Level 1 (温和): 1-3 次
         * - Level 2 (标准): 4-6 次
         * - Level 3 (强制): 7+ 次
         */
        var ALERT_LEVEL_THRESHOLD_1 = 3
        var ALERT_LEVEL_THRESHOLD_2 = 6
    }

    /**
     * 检查是否应该发送提醒
     * 
     * @param packageName 应用包名
     * @return true 表示可以提醒，false 表示应该跳过
     */
    fun shouldAlert(packageName: String): Boolean {
        // 1. 检查并重置每日计数
        checkAndResetDaily()

        // 2. 检查每日最大次数
        val todayCount = dailyAlertCountMap[packageName] ?: 0
        if (todayCount >= Config.MAX_DAILY_ALERTS) {
            Log.d(TAG, "$packageName 今日提醒次数已达上限: $todayCount")
            return false
        }

        // 3. 检查提醒间隔
        val lastAlertTime = lastAlertTimeMap[packageName] ?: 0
        val now = System.currentTimeMillis()
        val timeSinceLastAlert = now - lastAlertTime

        if (timeSinceLastAlert < Config.MIN_ALERT_INTERVAL_MS) {
            Log.d(TAG, "$packageName 提醒间隔太短: ${timeSinceLastAlert / 1000}秒")
            return false
        }

        return true
    }

    /**
     * 记录提醒发生
     * 
     * @param packageName 应用包名
     */
    fun recordAlert(packageName: String) {
        val now = System.currentTimeMillis()

        // 更新上次提醒时间
        lastAlertTimeMap[packageName] = now

        // 增加今日提醒次数
        val currentCount = dailyAlertCountMap[packageName] ?: 0
        dailyAlertCountMap[packageName] = currentCount + 1

        // 增加连续提醒次数
        val consecutiveCount = consecutiveAlertMap[packageName] ?: 0
        consecutiveAlertMap[packageName] = consecutiveCount + 1

        Log.d(TAG, "$packageName 提醒已记录")
        Log.d(TAG, "  - 今日次数: ${dailyAlertCountMap[packageName]}")
        Log.d(TAG, "  - 连续次数: ${consecutiveAlertMap[packageName]}")
    }

    /**
     * 获取当前提醒级别
     * 
     * @param packageName 应用包名
     * @return 提醒级别 (1=温和, 2=标准, 3=强制)
     */
    fun getAlertLevel(packageName: String): Int {
        val consecutiveCount = consecutiveAlertMap[packageName] ?: 0

        return when {
            consecutiveCount >= Config.ALERT_LEVEL_THRESHOLD_2 -> 3 // 强制
            consecutiveCount >= Config.ALERT_LEVEL_THRESHOLD_1 -> 2 // 标准
            else -> 1 // 温和
        }
    }

    /**
     * 重置应用的提醒状态
     * 
     * @param packageName 应用包名
     */
    fun resetAlertState(packageName: String) {
        consecutiveAlertMap.remove(packageName)
        Log.d(TAG, "$packageName 提醒状态已重置")
    }

    /**
     * 检查是否在免打扰时段
     * 
     * TODO: 后续可以实现具体的免打扰时段逻辑
     * 
     * @return true 表示在免打扰时段
     */
    fun isQuietHours(): Boolean {
        // 暂时返回 false，后续可以根据用户配置实现
        return false
    }

    /**
     * 获取统计信息
     * 
     * @param packageName 应用包名
     * @return 包含提醒统计的字符串
     */
    fun getStats(packageName: String): String {
        val todayCount = dailyAlertCountMap[packageName] ?: 0
        val consecutiveCount = consecutiveAlertMap[packageName] ?: 0
        val alertLevel = getAlertLevel(packageName)

        return buildString {
            appendLine("提醒统计: $packageName")
            appendLine("  - 今日次数: $todayCount / ${Config.MAX_DAILY_ALERTS}")
            appendLine("  - 连续次数: $consecutiveCount")
            appendLine("  - 提醒级别: $alertLevel")
        }
    }

    /**
     * 检查并重置每日计数
     */
    private fun checkAndResetDaily() {
        val currentDate = getCurrentDate()

        if (currentDate != lastResetDate) {
            Log.d(TAG, "检测到新的一天，重置所有计数")
            dailyAlertCountMap.clear()
            consecutiveAlertMap.clear()
            lastResetDate = currentDate
        }
    }

    /**
     * 获取当前日期字符串 (yyyy-MM-dd)
     */
    private fun getCurrentDate(): String {
        val calendar = java.util.Calendar.getInstance()
        return String.format(
            Locale.getDefault(),
            "%04d-%02d-%02d",
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH) + 1,
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * 清除所有数据
     */
    fun clearAll() {
        lastAlertTimeMap.clear()
        dailyAlertCountMap.clear()
        consecutiveAlertMap.clear()
        Log.d(TAG, "所有提醒数据已清除")
    }
}
