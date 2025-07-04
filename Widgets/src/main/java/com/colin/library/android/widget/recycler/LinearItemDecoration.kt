package com.colin.library.android.widget.recycler

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.withSave
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
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
    private val space: Int = 0,
    private val drawable: Drawable = Color.TRANSPARENT.toDrawable(),
    private val drawEdge: Boolean = true
) : ItemDecoration() {


    private val bounds = Rect()


    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val manager = parent.layoutManager as? LinearLayoutManager ?: return
        if (manager.orientation == RecyclerView.HORIZONTAL) {
            outRect[0, 0, getSpaceSize(RecyclerView.HORIZONTAL)] = 0
        } else {
            outRect[0, 0, 0] = getSpaceSize(RecyclerView.VERTICAL)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val manager = parent.layoutManager as? LinearLayoutManager ?: return
        if (manager.orientation == RecyclerView.HORIZONTAL) {
            drawHorizontal(canvas, parent, state)
        } else {
            drawVertical(canvas, parent, state)
        }
    }


    private fun drawVertical(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.withSave {
            val left: Int
            val right: Int
            if (parent.clipToPadding) {
                left = parent.paddingLeft
                right = parent.width - parent.paddingRight
                clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
            } else {
                left = 0
                right = parent.width
            }
            val count = parent.childCount
            val last = state.itemCount - 1
            for (i in 0 until count) {
                val child = parent.getChildAt(i)
                val childPosition = parent.getChildAdapterPosition(child)
                if (drawEdge || childPosition < last) {
                    parent.getDecoratedBoundsWithMargins(child, bounds)
                    val bottom = bounds.bottom + child.translationY.roundToInt()
                    val top = bottom - getSpaceSize(RecyclerView.VERTICAL)
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(this)
                }
            }
        }
    }

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
            val count = parent.childCount
            val last = state.itemCount - 1
            for (i in 0 until count) {
                val child = parent.getChildAt(i)
                val childPosition = parent.getChildAdapterPosition(child)
                if (drawEdge || childPosition < last) {
                    parent.layoutManager!!.getDecoratedBoundsWithMargins(child, bounds)
                    val right = bounds.right + child.translationX.roundToInt()
                    val left = right - getSpaceSize(RecyclerView.HORIZONTAL)
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(this)
                }
            }
        }
    }

    private fun getSpaceSize(orientation: Int = RecyclerView.HORIZONTAL): Int {
        if (space > 0) return space
        return if (orientation == RecyclerView.VERTICAL) drawable.intrinsicHeight else drawable.intrinsicWidth
    }

}