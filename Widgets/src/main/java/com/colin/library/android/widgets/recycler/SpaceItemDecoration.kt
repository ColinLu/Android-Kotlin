package com.colin.library.android.widgets.recycler

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.colin.library.android.utils.Constants

/**
 * 作者： ColinLu
 * 时间： 2022-02-15 23:17
 *
 *
 * 描述： Item 之间的间隔
 */
class SpaceItemDecoration(
    @field:Orientation var orientation: Int = RecyclerView.VERTICAL,
    @field:Px var space : Int= Constants.ZERO,
    var drawEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (parent.layoutManager == null || state.itemCount == Constants.ZERO) return
        if (orientation == RecyclerView.HORIZONTAL) {
            getHorizontalItemOffsets(outRect, view, parent, state)
        } else if (orientation == RecyclerView.VERTICAL) {
            getVerticalItemOffsets(outRect, view, parent, state)
        } else {
            val orientation = getManagerOrientation(parent)
            if (orientation == RecyclerView.HORIZONTAL) {
                getHorizontalItemOffsets(outRect, view, parent, state)
            } else if (orientation == RecyclerView.VERTICAL) {
                getVerticalItemOffsets(outRect, view, parent, state)
            }
        }
    }

    private fun getManagerOrientation(recyclerView: RecyclerView): Int {
        val manager = recyclerView.layoutManager
        if (manager is LinearLayoutManager) {
            return manager.orientation
        }
        return RecyclerView.VERTICAL
    }

    private fun getHorizontalItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val itemCount = state.itemCount
        if (itemCount == Constants.ZERO) return
        val position = parent.getChildAdapterPosition(view)
        val size = space shr 1
        when (position) {
            //first
            Constants.ZERO -> {
                outRect[if (drawEdge) space else Constants.ZERO, Constants.ZERO, size] =
                    Constants.ZERO
            }
            //last
            itemCount - 1 -> {
                outRect[size, Constants.ZERO, if (drawEdge) space else Constants.ZERO] =
                    Constants.ZERO
            }

            else -> outRect[size, Constants.ZERO, size] = Constants.ZERO
        }
    }

    private fun getVerticalItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {

        val itemCount = state.itemCount
        if (itemCount == Constants.ZERO) return
        val position = parent.getChildAdapterPosition(view)
        val size = space shr 1
        when (position) {
            //first
            Constants.ZERO -> {
                outRect[Constants.ZERO, if (drawEdge) space else Constants.ZERO, Constants.ZERO] =
                    size
            }
            //last
            itemCount - 1 -> {
                outRect[Constants.ZERO, size, Constants.ZERO] =
                    if (drawEdge) space else Constants.ZERO
            }

            else -> outRect[Constants.ZERO, size, Constants.ZERO] = size
        }
    }
}