package com.colin.library.android.widget.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.withSave
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.Orientation
import kotlin.math.roundToInt

/**
 * LinearItemDecoration 是 RecyclerView.ItemDecoration 的实现类
 * 用于在 LinearLayoutManager 的列表项之间添加分隔线
 * 
 * 支持功能：
 * - 横向和纵向布局
 * - 自定义分隔线间距
 * - 自定义分隔线Drawable（颜色、图片等）
 * - 控制首尾位置是否显示分隔线
 * 
 * 使用示例：
 * ```kotlin
 * // 基础用法
 * val decoration = LinearItemDecoration(context)
 * recyclerView.addItemDecoration(decoration)
 * 
 * // 自定义间距和颜色
 * val decoration = LinearItemDecoration(context)
 *     .setSpacing(16.dp())
 *     .setColor(Color.GRAY)
 * recyclerView.addItemDecoration(decoration)
 * 
 * // 使用Drawable
 * val decoration = LinearItemDecoration(context)
 *     .setDrawable(ContextCompat.getDrawable(context, R.drawable.divider))
 *     .setDrawStartEdgeDivider(true)
 *     .setDrawEndEdgeDivider(false)
 * recyclerView.addItemDecoration(decoration)
 * ```
 */
class LinearItemDecoration(
    private val context: Context, @Orientation orientation: Int = RecyclerView.VERTICAL
) : ItemDecoration() {

    private val rounds = Rect()

    /** 分隔线Drawable */
    private var drawable: Drawable? = null

    /** 分隔线间距（像素） */
    private var spacing: Int = 0

    /** 是否绘制第一个item之前的分隔线 */
    private var drawStartEdgeDivider: Boolean = false

    /** 是否绘制最后一个item之后的分隔线 */
    private var drawEndEdgeDivider: Boolean = false

    /** 当前方向：HORIZONTAL 或 VERTICAL */
    private var orientation = RecyclerView.VERTICAL

    init {
        setOrientation(orientation)
    }

    /**
     * 设置方向
     * 
     * @param orientation [RecyclerView.HORIZONTAL] 或 [RecyclerView.VERTICAL]
     * @return 当前实例，支持链式调用
     */
    fun setOrientation(@Orientation orientation: Int): LinearItemDecoration {
        require(orientation == RecyclerView.HORIZONTAL || orientation == RecyclerView.VERTICAL) {
            "Invalid orientation. Must be HORIZONTAL or VERTICAL"
        }
        this.orientation = orientation
        return this
    }

    /**
     * 设置分隔线Drawable
     * 
     * @param drawable 用作分隔线的Drawable
     * @return 当前实例，支持链式调用
     */
    fun setDrawable(drawable: Drawable?): LinearItemDecoration {
        this.drawable = drawable
        return this
    }

    /**
     * 设置分隔线颜色（会创建纯色Drawable）
     * 
     * @param color 分隔线颜色值
     * @return 当前实例，支持链式调用
     */
    @SuppressLint("UseKtx")
    fun setColor(@ColorInt color: Int): LinearItemDecoration {
        this.drawable = color.toDrawable()
        return this
    }

    /**
     * 设置分隔线间距
     * 
     * @param spacing 间距值（像素）
     * @return 当前实例，支持链式调用
     */
    fun setSpacing(spacing: Int): LinearItemDecoration {
        require(spacing >= 0) { "Spacing must be non-negative" }
        this.spacing = spacing
        return this
    }

    /**
     * 设置是否在第一个item之前绘制分隔线
     * 
     * @param draw true表示绘制，false表示不绘制
     * @return 当前实例，支持链式调用
     */
    fun setDrawStartEdgeDivider(draw: Boolean): LinearItemDecoration {
        this.drawStartEdgeDivider = draw
        return this
    }

    /**
     * 设置是否在最后一个item之后绘制分隔线
     * 
     * @param draw true表示绘制，false表示不绘制
     * @return 当前实例，支持链式调用
     */
    fun setDrawEndEdgeDivider(draw: Boolean): LinearItemDecoration {
        this.drawEndEdgeDivider = draw
        return this
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || drawable == null) return
        if (orientation == RecyclerView.HORIZONTAL) {
            drawHorizontal(canvas, parent, state)
        } else {
            drawVertical(canvas, parent, state)
        }
    }

    /**
     * 判断是否应该在指定位置绘制分隔线
     * 
     * @param adapterPosition item在adapter中的位置
     * @param itemCount 总item数量
     * @return true表示应该绘制，false表示不绘制
     */
    private fun shouldDrawDivider(adapterPosition: Int, itemCount: Int): Boolean {
        // 如果既不需要绘制首部分隔线，也不需要绘制尾部分隔线
        if (!drawStartEdgeDivider && !drawEndEdgeDivider) {
            // 只绘制中间的分隔线（不在第一个之前，也不在最后一个之后）
            return adapterPosition > 0 && adapterPosition < itemCount - 1
        }

        // 如果需要绘制首部分隔线
        if (drawStartEdgeDivider && adapterPosition == 0) {
            return true
        }

        // 如果需要绘制尾部分隔线
        if (drawEndEdgeDivider && adapterPosition == itemCount - 1) {
            return true
        }

        // 其他情况：绘制item之间的分隔线
        return adapterPosition > 0
    }

    @SuppressLint("UseKtx")
    private fun drawVertical(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.withSave {
            val left: Int
            val right: Int
            if (parent.clipToPadding) {
                left = parent.paddingLeft
                right = parent.width - parent.paddingRight
                clipRect(
                    left, parent.paddingTop, right, parent.height - parent.paddingBottom
                )
            } else {
                left = 0
                right = parent.width
            }

            val childCount = parent.childCount
            val itemCount = state.itemCount

            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val adapterPosition = parent.getChildAdapterPosition(child)

                if (adapterPosition == RecyclerView.NO_POSITION) continue

                // 判断是否应该在此位置绘制分隔线
                if (!shouldDrawDivider(adapterPosition, itemCount)) continue

                parent.getDecoratedBoundsWithMargins(child, rounds)

                // 计算分隔线位置
                val bottom = if (adapterPosition == 0 && drawStartEdgeDivider) {
                    // 第一个item之前：在top位置绘制
                    rounds.top + child.translationY.roundToInt()
                } else {
                    // 其他位置：在bottom位置绘制
                    rounds.bottom + child.translationY.roundToInt()
                }

                val top = bottom - getDividerHeight()
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(this)
            }
        }
    }

    @SuppressLint("UseKtx")
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.withSave {
            val top: Int
            val bottom: Int
            if (parent.clipToPadding) {
                top = parent.paddingTop
                bottom = parent.height - parent.paddingBottom
                clipRect(
                    parent.paddingLeft, top, parent.width - parent.paddingRight, bottom
                )
            } else {
                top = 0
                bottom = parent.height
            }

            val childCount = parent.childCount
            val itemCount = state.itemCount

            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val adapterPosition = parent.getChildAdapterPosition(child)

                if (adapterPosition == RecyclerView.NO_POSITION) continue

                // 判断是否应该在此位置绘制分隔线
                if (!shouldDrawDivider(adapterPosition, itemCount)) continue

                parent.layoutManager!!.getDecoratedBoundsWithMargins(child, rounds)

                // 计算分隔线位置
                val right = if (adapterPosition == 0 && drawStartEdgeDivider) {
                    // 第一个item之前：在left位置绘制
                    rounds.left + child.translationX.roundToInt()
                } else {
                    // 其他位置：在right位置绘制
                    rounds.right + child.translationX.roundToInt()
                }

                val left = right - getDividerWidth()
                drawable!!.setBounds(left, top, right, bottom)
                drawable!!.draw(this)
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (drawable == null && spacing == 0) {
            outRect.set(0, 0, 0, 0)
            return
        }

        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            outRect.set(0, 0, 0, 0)
            return
        }

        val itemCount = state.itemCount

        if (orientation == RecyclerView.HORIZONTAL) {
            // 水平方向
            val dividerWidth = getDividerWidth()

            // 左侧间距
            val leftSpacing = when {
                position == 0 && drawStartEdgeDivider -> dividerWidth
                position > 0 -> dividerWidth
                else -> 0
            }

            // 右侧间距
            val rightSpacing =
                if (position == itemCount - 1 && drawEndEdgeDivider) dividerWidth else 0

            outRect.set(leftSpacing, 0, rightSpacing, 0)
        } else {
            // 垂直方向
            val dividerHeight = getDividerHeight()

            // 顶部间距
            val topSpacing = when {
                position == 0 && drawStartEdgeDivider -> dividerHeight
                position > 0 -> dividerHeight
                else -> 0
            }

            // 底部间距
            val bottomSpacing =
                if (position == itemCount - 1 && drawEndEdgeDivider) dividerHeight else 0

            outRect.set(0, topSpacing, 0, bottomSpacing)
        }
    }

    /**
     * 获取分隔线高度（优先使用spacing，其次使用drawable的intrinsicHeight）
     */
    private fun getDividerHeight(): Int {
        return if (spacing > 0) spacing else drawable?.intrinsicHeight ?: 0
    }

    /**
     * 获取分隔线宽度（优先使用spacing，其次使用drawable的intrinsicWidth）
     */
    private fun getDividerWidth(): Int {
        return if (spacing > 0) spacing else drawable?.intrinsicWidth ?: 0
    }
}