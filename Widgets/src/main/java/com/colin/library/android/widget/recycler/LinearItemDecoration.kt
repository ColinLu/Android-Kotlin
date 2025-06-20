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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.Orientation
import kotlin.math.roundToInt

/**
 * DividerItemDecoration is a [RecyclerView.ItemDecoration] that can be used as a divider
 * between items of a [LinearLayoutManager]. It supports both [RecyclerView.HORIZONTAL] and
 * [RecyclerView.VERTICAL] orientations.
 *
 * <pre>
 * mDividerItemDecoration = new LinearItemDecoration(recyclerView.getContext(),
 * mLayoutManager.getOrientation());
 * recyclerView.addItemDecoration(mDividerItemDecoration);
</pre> *
 */
class LinearItemDecoration(
    private val context: Context, @Orientation orientation: Int = RecyclerView.VERTICAL
) : ItemDecoration() {
    private val mBounds = Rect()
    /**
     * @return the [Drawable] for this divider.
     */
    private var drawable: Drawable? = null

    private var drawLastPositionDivider = true

    /**
     * Current orientation. Either [RecyclerView.HORIZONTAL] or [RecyclerView.VERTICAL].
     */
    private var orientation = RecyclerView.VERTICAL

    /**
     * Sets the orientation for this divider. This should be called if
     * [RecyclerView.LayoutManager] changes orientation.
     *
     * @param orientation [RecyclerView.HORIZONTAL] or [RecyclerView.VERTICAL]
     */
    fun setOrientation(@Orientation orientation: Int) {
        this.orientation = orientation
    }

    /**
     * Sets the [Drawable] for this divider.
     *
     * @param drawable Drawable that should be used as a divider.
     */
    fun setDrawable(drawable: Drawable?) {
        this.drawable = drawable
    }

    @SuppressLint("UseKtx")
    fun setColor(@ColorInt color: Int) {
        this.drawable = color.toDrawable()
    }

    fun isDrawLastPositionDivider(isDraw: Boolean) {
        drawLastPositionDivider = isDraw
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || drawable == null) return
        if (orientation == RecyclerView.HORIZONTAL) {
            drawHorizontal(canvas, parent, state)
        } else {
            drawVertical(canvas, parent, state)
        }
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
            val lastPosition = state.itemCount - 1
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val childRealPosition = parent.getChildAdapterPosition(child)
                if (drawLastPositionDivider || childRealPosition < lastPosition) {
                    parent.getDecoratedBoundsWithMargins(child, mBounds)
                    val bottom = mBounds.bottom + child.translationY.roundToInt()
                    val top = bottom - drawable!!.intrinsicHeight
                    drawable!!.setBounds(left, top, right, bottom)
                    drawable!!.draw(this)
                }
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
            val lastPosition = state.itemCount - 1
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val childRealPosition = parent.getChildAdapterPosition(child)
                if (drawLastPositionDivider || childRealPosition < lastPosition) {
                    parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
                    val right = mBounds.right + child.translationX.roundToInt()
                    val left = right - drawable!!.intrinsicWidth
                    drawable!!.setBounds(left, top, right, bottom)
                    drawable!!.draw(this)
                }
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (drawable == null) {
            outRect[0, 0, 0] = 0
            return
        }
        if (orientation == RecyclerView.HORIZONTAL) {
            outRect[0, 0, drawable!!.intrinsicWidth] = 0
        } else {
            outRect[0, 0, 0] = drawable!!.intrinsicHeight
        }
    }

    companion object {
//        private val ATTRS = intArrayOf(R.attr.listDivider)
    }

    /**
     * Creates a divider [RecyclerView.ItemDecoration] that can be used with a
     * [LinearLayoutManager].
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be [RecyclerView.HORIZONTAL] or [RecyclerView.VERTICAL].
     */
    init {
//        val a = context.obtainStyledAttributes(ATTRS)
//        drawable = a.getDrawable(0)
//        a.recycle()
//        setOrientation(orientation)
    }
}