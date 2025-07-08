package com.colin.library.android.widget.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.colin.library.android.utils.Log
import kotlin.math.min

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-07-08 16:12
 *
 * Des   :GestureImageView
 */
class GestureImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    private var isZoomed = false
    private var scaleFactor = 2f // 放大倍数
    private var SCALE_DEFAULT = 1f

    /**
     * 双指缩放时调用，方法调用顺序:
     * 1. onScaleBegin(detector)   //1、缩放开始时调用
     * 2. onScale(detector)        //2、每一帧的缩放变动会调用多次
     * 3. ...
     * 4. ...
     * 5. onScale(...)
     * 6. onScaleEnd(detector)     //3、缩放结束（手指离开）时调用
     */
    private val scaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {

        /**
         * 当两个手指在屏幕上移动时持续调用。在这个方法中可以通过detector.getScaleFactor()获取缩放因子，更新视图的缩放逻辑
         */
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Log.i("onScale:$currentScale")
            val preScale = currentScale
            //detector.scaleFactor 表示当前帧的缩放因子（如1.05表示放大5%，0.95表示缩小5%）
            currentScale = (currentScale * detector.scaleFactor).coerceIn(0.5f, 3.0f) //限制缩放比例
            val realFactor = currentScale / preScale //计算限制后的实际缩放因子：新的/旧的,进行缩放矫正
            //focusX/focusY表示两个手指的中心点（焦点）坐标
            val focusX = detector.focusX
            val focusY = detector.focusY
            isZoomed = (currentScale > SCALE_DEFAULT)
            matrix.postScale(realFactor, realFactor, focusX, focusY)
            imageMatrix = matrix
            return true
        }

        /**
         * 缩放手势开始时调用
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        /**
         * 缩放手势结束时调用
         */
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            Log.i("onScaleEnd:$currentScale")
        }
    }

    private val dragGestureListener = object : GestureDetector.SimpleOnGestureListener() {

        //-----------------GestureDetector.OnGestureListener start-----------------
        override fun onDown(e: MotionEvent): Boolean {
            Log.i("onDown:${e.actionMasked}")
            return true
        }

        /**
         * 手指按下后，短暂静止时触发（未抬起/滑动），用于提供视觉提示
         */
        override fun onShowPress(e: MotionEvent) {
            Log.i("onShowPress:${e.actionMasked}")
        }

        /**
         * 户点击（按下再抬起）时调用。 true：表示消费了这个点击事件
         */
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Log.i("onSingleTapUp:${e.actionMasked}")
            return false
        }

        /**
         * 拖动手势中持续触发，表示手指在滑动
         *
         * @param e1
         * @param e2
         * @param distanceX
         * @param distanceY
         * distanceX 和 distanceY 表示当前事件和前一个事件之间的移动距离（不是总距离）。手指滑动方向：
         * distanceX：👉 向右滑动为负数，x减小；👈 向左滑动正数，x增加  （preX - curX）
         * distanceY：👇 向下滑动负数，y减小；👆 向上滑动正数，y增加  (preY - curY)
         */
        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            Log.i("onScroll() -> e1:${e1?.actionMasked}, e2:${e2.actionMasked},distanceX:$distanceX,distanceY:$distanceY")
            matrix.postTranslate(-distanceX, -distanceY)
            imageMatrix = matrix
            return true
        }

        /**
         * 用户按住屏幕超过一定时间（默认 500ms）且未滑动，会触发此方法
         */
        override fun onLongPress(e: MotionEvent) {
            Log.i("onLongPress():${e.actionMasked}")
        }

        /**
         * 快速滑动后抬起手指（惯性滑动）时触发
         *
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return true表示消费了这个fling手势
         */
        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
        ): Boolean {
            Log.i("onFling() -> e1:${e1?.actionMasked}, e2:${e2.actionMasked},velocityX:$velocityX,velocityY:$velocityY")
            return false
        }
        //-----------------GestureDetector.OnGestureListener end-----------------
        //-----------------GestureDetector.OnDoubleTapListener start-----------------

        /**
         * 快速点击两次，触发该方法
         */
        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.i("onDoubleTap():${e.actionMasked}")
            zoomImage()
            return true
        }

        /**
         * 在双击过程中，down, move, up 都会回调到这里
         */
        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            Log.i("onDoubleTapEvent:${e.actionMasked}")
            return super.onDoubleTapEvent(e)
        }

        /**
         * 确认是单击（而非双击的一部分）时触发
         */
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Log.i("onSingleTapConfirmed:${e.actionMasked}")
            return super.onSingleTapConfirmed(e)
        }
        //-----------------GestureDetector.OnDoubleTapListener end-----------------
        //-----------------GestureDetector.OnContextClickListener start-----------------

        /**
         * 支持鼠标右键或触控板的 context click（上下文点击）,适用于 Android TV 或外接鼠标设备
         */
        override fun onContextClick(e: MotionEvent): Boolean {
            Log.i("onContextClick:${e.actionMasked}")
            return super.onContextClick(e)
        }
        //-----------------GestureDetector.OnContextClickListener end-----------------
    }

    private val rotationGestureListener = object : RotationGestureDetector.OnRotationListener {
        override fun onRotation(rotationDetector: RotationGestureDetector, angle: Float) {
            currentRotation += angle
            val px = width / 2f
            val py = height / 2f
            matrix.postRotate(angle, px, py)
            imageMatrix = matrix
        }
    }

    private val matrix = Matrix()
    private var currentScale = SCALE_DEFAULT //当前缩放了多少倍
    private var currentRotation = 0f

    private var scaleGestureDetector: ScaleGestureDetector
    private var gestureDetector: GestureDetector
    private var rotationGestureDetector: RotationGestureDetector

    init {
        scaleType = ScaleType.MATRIX
        scaleGestureDetector = ScaleGestureDetector(context, scaleGestureListener)
        gestureDetector = GestureDetector(context, dragGestureListener)
        rotationGestureDetector = RotationGestureDetector(rotationGestureListener)
        toCenter(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        rotationGestureDetector.onTouchEvent(event)
        return true
    }

    private fun zoomImage() {
        Log.i("currentScale:$currentScale,isZoomed:$isZoomed")
        if (currentScale == SCALE_DEFAULT) {
            scaleFactor = if (isZoomed) 0.5f else 2f
            matrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
            isZoomed = !isZoomed
            imageMatrix = matrix
            invalidate()
        } else {
            toCenter(this)
            currentScale = SCALE_DEFAULT
            isZoomed = false
        }
    }

    private fun getCurScale(): Float {
        val mArr = FloatArray(9)
        imageMatrix.getValues(mArr)
        return min(mArr[Matrix.MSCALE_X], mArr[Matrix.MSCALE_Y])
    }

    private fun toCenter(img: ImageView) {
        img.post {
            val drawable = img.drawable //图片
            drawable?.let {
                matrix.reset()
                //计算宽高比例
                val widthScale = img.width.toFloat() / it.intrinsicWidth
                val heightScale = img.height.toFloat() / it.intrinsicHeight
                //选择较小的缩放比
                val scale = min(widthScale, heightScale)
                //缩放后的图片尺寸
                val scaledWidth = it.intrinsicWidth * scale
                val scaleHeight = it.intrinsicHeight * scale
                //计算平移量
                val dx = (img.width - scaledWidth) / 2
                val dy = (img.height - scaleHeight) / 2
                matrix.postScale(scale, scale)
                matrix.postTranslate(dx, dy)
                img.imageMatrix = matrix
            }
        }
    }
}