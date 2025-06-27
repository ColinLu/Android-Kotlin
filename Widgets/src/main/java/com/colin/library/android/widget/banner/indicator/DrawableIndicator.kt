package com.colin.library.android.widget.banner.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat

/**
 * 图片选择器
 */
class DrawableIndicator @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : IIndicatorView(context!!, attrs, defStyleAttr) {
    // 选中时的Bitmap
    private var selectBitmap: Bitmap? = null

    // 未选中时的Bitmap
    private var normalBitmap: Bitmap? = null
    private var normalResize = true
    private var selectResize = true
    override fun onMeasure(
        widthMeasureSpec: Int, heightMeasureSpec: Int
    ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width =
            getSelectWidth() + (getNormalWidth() + getIndicatorSpace()) * (getItemCount() - 1)
        val height = getSelectHeight().coerceAtLeast(getNormalHeight())
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        if (getItemCount() > 0 && selectBitmap != null && normalBitmap != null) {
            for (i in 1 until getItemCount() + 1) {
                var left: Float
                var top: Float
                var bitmap = normalBitmap!!
                val index = i - 1
                when {
                    index < getPosition() -> {
                        left = (i - 1) * (getNormalWidth() + getIndicatorSpace())
                        top = measuredHeight / 2 - getNormalHeight() / 2
                    }

                    index == getPosition() -> {
                        left = (i - 1) * (getNormalWidth() + getIndicatorSpace())
                        top = measuredHeight / 2 - getSelectHeight() / 2
                        bitmap = selectBitmap!!
                    }

                    else -> {
                        left =
                            (i - 1) * getIndicatorSpace() + (i - 2) * getNormalWidth() + getSelectWidth()
                        top = measuredHeight / 2 - getNormalHeight() / 2
                    }
                }
                drawIcon(canvas, left, top, bitmap)
            }
        }
    }

    private fun drawIcon(
        canvas: Canvas, left: Float, top: Float, icon: Bitmap?
    ) {
        if (icon == null || icon.width <= 0 || icon.height <= 0) {
            return
        }
        canvas.drawBitmap(icon, left, top, null)
    }

    private fun initIconSize() {
        selectBitmap?.let { bitmap ->
            if (bitmap.isMutable && selectResize) {
                bitmap.width = getSelectWidth().toInt()
                bitmap.height = getSelectHeight().toInt()
            } else {
                val width = bitmap.width
                val height = bitmap.height
                val scaleWidth = getSelectWidth() / width
                val scaleHeight = getSelectHeight() / height
                val matrix = Matrix()
                matrix.postScale(scaleWidth, scaleHeight)
                selectBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            }
            setSelect(selectBitmap?.width?.toFloat() ?: 0F, selectBitmap?.height?.toFloat() ?: 0F)
        }
        normalBitmap?.let { bitmap ->
            if (bitmap.isMutable && selectResize) {
                bitmap.width = getNormalWidth().toInt()
                bitmap.height = getNormalHeight().toInt()
            } else {
                val width = bitmap.width
                val height = bitmap.height
                val scaleWidth = getNormalWidth() / width
                val scaleHeight = getNormalHeight() / height
                val matrix = Matrix()
                matrix.postScale(scaleWidth, scaleHeight)
                normalBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            }
            setNormal(normalBitmap?.width?.toFloat() ?: 0F, normalBitmap?.height?.toFloat() ?: 0F)
        }
    }

    fun setDrawable(
        @DrawableRes selected: Int, @DrawableRes normal: Int
    ): DrawableIndicator {
        normalBitmap = BitmapFactory.decodeResource(resources, normal)
        selectBitmap = BitmapFactory.decodeResource(resources, selected)
        if (normalBitmap == null) {
            normalBitmap = getBitmapFromDrawable(context, normal)
            normalResize = false
        }
        if (selectBitmap == null) {
            selectBitmap = getBitmapFromDrawable(context, selected)
            selectResize = false
        }
        initIconSize()
        postInvalidate()
        return this
    }

    fun setDrawable(
        selected: Bitmap, normal: Bitmap
    ): DrawableIndicator {
        if (normalBitmap == null) {
            normalBitmap = normal
            normalResize = false
        }
        if (selectBitmap == null) {
            selectBitmap = selected
            selectResize = false
        }
        initIconSize()
        postInvalidate()
        return this
    }

    @SuppressLint("UseKtx")
    private fun getBitmapFromDrawable(
        context: Context, @DrawableRes res: Int
    ): Bitmap? {
        var drawable = ContextCompat.getDrawable(context, res)
        if (drawable != null) drawable = DrawableCompat.wrap(drawable).mutate()
        drawable?.let {
            val bitmap = createBitmap(it.intrinsicWidth, it.intrinsicHeight)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
            return bitmap
        }
        return null
    }
}