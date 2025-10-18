package com.colin.library.android.widget.banner.indicator

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ZERO
import com.colin.library.android.widget.INDICATOR_NORMAL_COLOR
import com.colin.library.android.widget.INDICATOR_NORMAL_WIDTH
import com.colin.library.android.widget.INDICATOR_RADIUS
import com.colin.library.android.widget.INDICATOR_SELECTED_COLOR
import com.colin.library.android.widget.INDICATOR_SELECTED_WIDTH
import com.colin.library.android.widget.INDICATOR_SPACE
import com.colin.library.android.widget.R
import com.colin.library.android.widget.banner.def.IndicatorMode
import com.colin.library.android.widget.banner.def.IndicatorStyle

/**
 * IIndicator
 */
abstract class IIndicatorView constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int
) : View(context, attrs, defStyleAttr), IIndicator {
    internal var itemCount: Int = ZERO
    internal var position: Int = ZERO

    @IndicatorMode
    internal var indicatorMode: Int = IndicatorMode.NORMAL

    @IndicatorStyle
    internal var indicatorStyle: Int = IndicatorStyle.CIRCLE

    @Px
    internal var selectWidth: Float = INDICATOR_SELECTED_WIDTH

    @Px
    internal var selectHeight: Float = INDICATOR_SELECTED_WIDTH

    @Px
    internal var normalWidth: Float = INDICATOR_NORMAL_WIDTH

    @Px
    internal var normalHeight: Float = INDICATOR_NORMAL_WIDTH

    @Px
    internal var selectRadius: Float = INDICATOR_RADIUS

    @Px
    internal var normalRadius: Float = INDICATOR_RADIUS

    @Px
    internal var indicatorSpace: Float = INDICATOR_SPACE

    @ColorInt
    internal var selectColor: Int = INDICATOR_SELECTED_COLOR

    @ColorInt
    internal var normalColor: Int = INDICATOR_NORMAL_COLOR

    @FloatRange(from = 0.0, to = 1.0)
    internal var scrolledOffset: Float = 0F

    internal var gravity: Int = Gravity.CENTER

    internal var orientation: Int = LinearLayout.HORIZONTAL


    init {
        context.withStyledAttributes(attrs, R.styleable.IndicatorView, 0, 0) {
            indicatorStyle = getInt(R.styleable.IndicatorView_indicatorStyle, indicatorStyle)
            indicatorMode = getInt(R.styleable.IndicatorView_indicatorMode, indicatorMode)
            selectWidth = getDimension(R.styleable.IndicatorView_indicatorSelectWidth, selectWidth)
            selectHeight =
                getDimension(R.styleable.IndicatorView_indicatorSelectHeight, selectHeight)
            selectRadius =
                getDimension(R.styleable.IndicatorView_indicatorSelectRadius, selectRadius)
            normalWidth = getDimension(R.styleable.IndicatorView_indicatorNormalWidth, normalWidth)
            normalHeight =
                getDimension(R.styleable.IndicatorView_indicatorNormalHeight, normalHeight)
            normalRadius =
                getDimension(R.styleable.IndicatorView_indicatorNormalRadius, normalRadius)
            indicatorSpace = getDimension(R.styleable.IndicatorView_indicatorSpace, indicatorSpace)
            selectColor = getColor(R.styleable.IndicatorView_indicatorSelectColor, selectColor)
            normalColor = getColor(R.styleable.IndicatorView_indicatorNormalColor, normalColor)
            gravity = getInt(R.styleable.IndicatorView_android_gravity, gravity)
            orientation = getInt(R.styleable.IndicatorView_android_orientation, orientation)
            visibility = getInt(R.styleable.IndicatorView_android_visibility, visibility)
        }
    }

    override fun onPageScrolled(
        position: Int, positionOffset: Float, @Px positionOffsetPixels: Int
    ) {
        this.position = position
        this.scrolledOffset = positionOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        Log.i("IndicatorView onPageSelected->position:$position")
        if (this.position != position) {
            this.position = position
            this.scrolledOffset = 0F
            invalidate()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        Log.i("IndicatorView onPageScrollStateChanged->state:$state")
    }

    override fun getView() = this

    override fun getIndicatorStyle() = indicatorStyle
    override fun setIndicatorStyle(@IndicatorStyle style: Int) {
        if (indicatorStyle != style) {
            indicatorStyle = style
            notifyDataChanged()
        }
    }

    override fun getIndicatorMode() = indicatorMode
    override fun setIndicatorMode(@IndicatorMode mode: Int) {
        if (indicatorMode != mode) {
            indicatorMode = mode
            notifyDataChanged()
        }
    }

    override fun getItemCount() = itemCount
    override fun setItemCount(count: Int) {
        if (itemCount != count) {
            itemCount = count
            notifyDataChanged()
        }
    }

    override fun getPosition() = position
    override fun setPosition(position: Int) {
        if (this.position != position) {
            this.position = position
            invalidate()
        }
    }

    override fun setItemCount(count: Int, position: Int) {
        if (this.itemCount != count || this.position != position) {
            this.itemCount = count
            this.position = position
            notifyDataChanged()
        }
    }

    @FloatRange(from = 0.0, to = 1.0)
    override fun getScrollProgress() = scrolledOffset

    @Px
    override fun getSelectWidth() = selectWidth

    @Px
    override fun getSelectHeight() = selectHeight
    override fun setSelect(@Px width: Float, @Px height: Float) {
        if (selectWidth != width || selectHeight != height) {
            selectWidth = width
            selectHeight = height
            notifyDataChanged()
        }
    }

    @Px
    override fun getSelectRadius() = selectRadius

    @Px
    override fun getNormalWidth() = normalWidth

    @Px
    override fun getNormalHeight() = normalHeight
    override fun setNormal(@Px width: Float, @Px height: Float) {
        if (normalWidth != width || normalHeight != height) {
            normalWidth = width
            normalHeight = height
            notifyDataChanged()
        }
    }

    @Px
    override fun getNormalRadius() = normalRadius
    override fun setRadius(@Px select: Float, @Px normal: Float) {
        if (selectRadius != select || normalRadius != normal) {
            selectRadius = select
            normalRadius = normal
            notifyDataChanged()
        }
    }

    @Px
    override fun getIndicatorSpace() = indicatorSpace
    override fun setIndicatorSpace(@Px space: Float) {
        if (indicatorSpace != space) {
            indicatorSpace = space
            notifyDataChanged()
        }
    }

    @ColorInt
    override fun getSelectColor() = selectColor

    @ColorInt
    override fun getNormalColor() = normalColor
    override fun setColor(@ColorInt select: Int, @ColorInt normal: Int) {
        if (selectColor != select || normalColor != normal) {
            selectColor = select
            normalColor = normal
            invalidate()
        }
    }

    override fun getOrientation() = orientation

    override fun setOrientation(orientation: Int) {
        if (this.orientation != orientation) {
            this.orientation = orientation
            notifyDataChanged()
        }
    }

    override fun getGravity() = gravity
    override fun setGravity(gravity: Int) {
        if (this.gravity != gravity) {
            this.gravity = gravity
            (getView().layoutParams as FrameLayout.LayoutParams).apply {
                this.gravity = gravity
            }
            notifyDataChanged()
        }
    }

    override fun notifyDataChanged() {
        if (showIndicator()) {
            requestLayout()
            invalidate()
        }
    }

    override fun showIndicator() = isVisible && itemCount > 0

}
