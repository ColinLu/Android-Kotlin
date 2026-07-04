package com.colin.nfc.focus.ui.provider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.colin.nfc.focus.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * 默认覆盖窗口提供者实现
 * 
 * 使用 Material Design 3 风格，支持深色模式自动适配
 * 
 * 特性：
 * - 全屏半透明覆盖
 * - 显示分心应用图标和名称
 * - 倒计时显示（可选）
 * - 三个操作按钮：返回专注、临时退出、关闭提醒
 */
class DefaultAlertViewProvider(
    private val context: Context
) : IAlertViewProvider {
    
    private var currentListener: AlertActionListener? = null
    
    override fun createOverlayView(context: Context): View {
        return LayoutInflater.from(context).inflate(
            R.layout.layout_focus_overlay_default,
            null
        )
    }
    
    override fun bindData(view: View, alertData: AlertData) {
        // 设置应用图标
        val ivAppIcon = view.findViewById<ImageView>(R.id.ivAppIcon)
        if (alertData.appIcon != null) {
            ivAppIcon.setImageDrawable(alertData.appIcon)
        } else {
            ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon)
        }
        
        // 设置应用名称
        val tvAppName = view.findViewById<TextView>(R.id.tvAppName)
        tvAppName.text = alertData.appName
        
        // 设置提醒消息
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = alertData.message ?: when (alertData.focusMode) {
            com.colin.nfc.focus.blocker.FocusMode.GENTLE -> "专注时间，请勿分心"
            com.colin.nfc.focus.blocker.FocusMode.BALANCED -> "检测到分心应用，建议返回专注"
            com.colin.nfc.focus.blocker.FocusMode.STRICT -> "严格模式：禁止使用此应用"
        }
        
        // 根据配置显示/隐藏关闭按钮
        val btnDismiss = view.findViewById<MaterialButton>(R.id.btnDismiss)
        btnDismiss.visibility = if (alertData.allowDismiss) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        // 如果设置了超时时间，显示倒计时
        if (alertData.timeoutSeconds > 0) {
            val tvCountdown = view.findViewById<TextView>(R.id.tvCountdown)
            tvCountdown.visibility = View.VISIBLE
            startCountdown(tvCountdown, alertData.timeoutSeconds)
        }
    }
    
    override fun setActionListener(view: View, listener: AlertActionListener) {
        currentListener = listener
        
        // 返回专注按钮
        view.findViewById<MaterialButton>(R.id.btnReturnToFocus).setOnClickListener {
            listener.onReturnToFocus()
        }
        
        // 临时退出按钮
        view.findViewById<MaterialButton>(R.id.btnTemporaryExit).setOnClickListener {
            listener.onTemporaryExit(5) // 默认 5 分钟
        }
        
        // 关闭提醒按钮
        view.findViewById<MaterialButton>(R.id.btnDismiss).setOnClickListener {
            listener.onDismissAlert()
        }
    }
    
    /**
     * 启动倒计时
     */
    private fun startCountdown(textView: TextView, seconds: Int) {
        var remaining = seconds
        
        val runnable = object : Runnable {
            override fun run() {
                if (remaining <= 0) {
                    textView.visibility = View.GONE
                    return
                }
                
                val minutes = remaining / 60
                val secs = remaining % 60
                textView.text = String.format("%02d:%02d", minutes, secs)
                
                remaining--
                textView.postDelayed(this, 1000)
            }
        }
        
        textView.post(runnable)
    }
}
