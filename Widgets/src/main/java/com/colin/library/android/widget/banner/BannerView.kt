package com.colin.library.android.widget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.BundleCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import com.colin.library.android.utils.INVALID
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ZERO
import com.colin.library.android.utils.ext.dp
import com.colin.library.android.widget.R
import com.colin.library.android.widget.banner.indicator.IIndicator
import com.colin.library.android.widget.banner.indicator.IndicatorView
import com.colin.library.android.widget.setClipViewCornerRadius
import kotlinx.coroutines.Runnable
import kotlin.math.abs

/**
 * BannerViewPager
 */
@SuppressLint("CutPasteId")
class BannerView @JvmOverloads constructor(
    private val context: Context, private val attrs: AttributeSet? = null
) : FrameLayout(context, attrs), DefaultLifecycleObserver {
    private lateinit var bannerPage: ViewPager2
    private lateinit var bannerIndicator: IIndicator

    companion object {
        private const val LIMIT_COUNT = 2
        private const val INTERVAL_TIME = 3000L
        private const val BANNER_INDEX = 0 //固定位置
        private const val INDICATOR_INDEX = 1 //固定位置
        private val INDICATOR_HEIGHT = 100.dp()
    }

    var radius = 0F
        set(value) {
            if (field != value) {
                field = value
                setClipViewCornerRadius(value)
            }
        }

    var cyclic = true
        set(value) {
            if (field != value) field = value
        }

    /*设置后记得手动开启 startPlay方法*/
    var autoPlay = true
        set(value) {
            if (field == value) return
            field = value
        }

    /*设置后记得手动开启 startPlay方法*/
    var interval = INTERVAL_TIME
        set(value) {
            if (field == value) return
            field = value
        }

    var orientation = ViewPager2.ORIENTATION_HORIZONTAL
        set(value) {
            if (field == value) return
            field = value
            if (::bannerPage.isInitialized) bannerPage.orientation = value
        }

    var limit = LIMIT_COUNT
        set(value) {
            if (field == value) return
            field = value
            if (::bannerPage.isInitialized) bannerPage.setCurrentItem(value)
        }

    var userInputEnabled = true
        set(value) {
            if (field == value) return
            field = value
            if (::bannerPage.isInitialized) bannerPage.isUserInputEnabled = value
        }

    var indicatorHeight = INDICATOR_HEIGHT
        set(value) {
            if (field == value) return
            field = value
            if (::bannerIndicator.isInitialized) {
                (bannerIndicator.getView().layoutParams as LayoutParams).apply { height = value }
                requestLayout()
            }
        }

    var indicatorWidth = MATCH_PARENT
        set(value) {
            if (field == value) return
            field = value
            if (::bannerIndicator.isInitialized) {
                (bannerIndicator.getView().layoutParams as LayoutParams).apply { width = value }
                requestLayout()
            }
        }

    var indicatorGravity = MATCH_PARENT
        set(value) {
            if (field == value) return
            field = value
            if (::bannerIndicator.isInitialized) {
                (bannerIndicator.getView().layoutParams as LayoutParams).apply { gravity = value }
                requestLayout()
            }
        }
    var indicatorOrientation = ViewPager2.ORIENTATION_HORIZONTAL
        set(value) {
            if (field == value) return
            field = value
            if (::bannerIndicator.isInitialized) {
                bannerIndicator.setOrientation(orientation)
            }
        }

    @ColorInt
    var indicatorBackgroundColor = context.getColor(R.color.transparent)
        set(value) {
            if (field == value) return
            field = value
            if (::bannerIndicator.isInitialized) {
                bannerIndicator.getView().background = value.toDrawable()
            }
        }

    var pageChangeListener: OnPageChangeCallback? = null

    private var bannerAdapter: AdapterWrapper<ViewHolder>? = null
    private var disallowInterceptTouchEvent = true
    private var mRadiusRectF: RectF? = null
    private var mRadiusPath: Path? = null

    private var downX = 0F
    private var downY = 0F


    private val touchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop / 2 }
    private val pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, offset: Float, pixels: Int) {
            val current = position % getRealItemCount()
            pageChangeListener?.onPageScrolled(current, offset, pixels)
            bannerIndicator.onPageScrolled(current, offset, pixels)
        }

        override fun onPageSelected(position: Int) {
            val current = position % getRealItemCount()
            Log.i("Selected-->>position:$position current:$current")
            if (getGlobalVisibleRect(Rect()) && windowVisibility == VISIBLE) {
                pageChangeListener?.onPageSelected(current)
                bannerIndicator.onPageSelected(current)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            val current = bannerPage.currentItem
            val count = getRealItemCount()
            Log.i("StateChanged-->>count:$count current:$current state:$state")
            //更新Indicator数量
            if (bannerIndicator.getItemCount() != count) {
                bannerIndicator.setItemCount(count)
            }
            //无限循环且cyclicCount>0
            if (state == ViewPager2.SCROLL_STATE_IDLE && cyclic && count > 1) {
                //
                if (current + 1 == Int.MAX_VALUE) {
                    bannerPage.setCurrentItem(getStartPage(current % count), false)
                }
                if (current < count) {
                    bannerPage.setCurrentItem(getStartPage(current % count), false)
                }
            }
            pageChangeListener?.onPageScrollStateChanged(state)
            bannerIndicator.onPageScrollStateChanged(state)
        }
    }
    private val playRunnable = object : Runnable {
        override fun run() {
            val count = getRealItemCount()
            Log.i("play interval:$interval count:$count current:${bannerPage.currentItem}")
            if (count > 1) {
                //边界已在onPageScrollStateChanged处理完了，所以不用担心数组越界
                bannerPage.currentItem = bannerPage.currentItem + 1
            }
            if (interval > 0) postDelayed(this, interval)
        }
    }

    init {
        //init attrs
        setWillNotDraw(false)
        context.withStyledAttributes(attrs, R.styleable.BannerView, 0, 0) {
            cyclic = getBoolean(R.styleable.BannerView_cyclic, cyclic)
            radius = getDimension(R.styleable.BannerView_radius, radius)
            autoPlay = getBoolean(R.styleable.BannerView_autoPlay, autoPlay)
            interval = getInteger(R.styleable.BannerView_interval, interval.toInt()).toLong()
            limit = getInteger(R.styleable.BannerView_limit, limit)
            disallowInterceptTouchEvent = getBoolean(
                R.styleable.BannerView_disallowInterceptTouchEvent, disallowInterceptTouchEvent
            )
            clipChildren = getBoolean(R.styleable.BannerView_android_clipChildren, clipChildren)

            orientation = getInt(
                R.styleable.BannerView_android_orientation, ViewPager2.ORIENTATION_HORIZONTAL
            )
            indicatorWidth =
                getLayoutDimension(R.styleable.BannerView_indicatorWidth, indicatorWidth)
            indicatorHeight =
                getLayoutDimension(R.styleable.BannerView_indicatorHeight, indicatorHeight)
            indicatorBackgroundColor =
                getColor(R.styleable.BannerView_indicatorBackgroundColor, indicatorBackgroundColor)
            indicatorGravity = getInt(R.styleable.BannerView_indicatorGravity, Gravity.BOTTOM)
        }
        //init view
        bannerPage = findViewById(R.id.banner_pager) ?: createBannerView()
        bannerIndicator = findViewById(R.id.banner_indicator) ?: createIndicatorView()

        bannerPage.registerOnPageChangeCallback(pageChangeCallback)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bannerAdapter = null
        stopPlay()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                stopPlay()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                startPlay()
            }

            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val doNotNeedIntercept = bannerPage.isUserInputEnabled != true || getRealItemCount() <= 1
        if (doNotNeedIntercept) return super.onInterceptTouchEvent(ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
                parent.requestDisallowInterceptTouchEvent(disallowInterceptTouchEvent)
            }

            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(isDragBanner(ev.x, ev.y))
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }

            else -> {}
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun isDragBanner(x: Float, y: Float): Boolean {
        val distanceX = abs(x - downX)
        val distanceY = abs(y - downY)
        return if (bannerPage.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            distanceY > touchSlop && distanceY > distanceX
        } else {
            distanceX > touchSlop && distanceX > distanceY
        }
    }


//    override fun dispatchDraw(canvas: Canvas) {
//        val roundRectRadiusArray: FloatArray? =
//            bannerManager.getBannerOptions().getRoundRectRadiusArray()
//        if ((mRadiusRectF != null) && (mRadiusPath != null) && (roundRectRadiusArray != null)) {
//            mRadiusRectF?.right = this.width.toFloat()
//            mRadiusRectF?.bottom = this.height.toFloat()
//            mRadiusPath?.addRoundRect(mRadiusRectF!!, roundRectRadiusArray, Path.Direction.CW)
//            canvas.clipPath(mRadiusPath!!)
//        }
//        super.dispatchDraw(canvas)
//    }

    fun setAdapter(adapter: RecyclerView.Adapter<out ViewHolder>?) {
        bannerAdapter =
            if (adapter == null) null else AdapterWrapper(adapter as RecyclerView.Adapter<ViewHolder>)
        bannerPage.adapter = bannerAdapter
        if (bannerAdapter == null) stopPlay()
        else startPlay()
    }


    private val KEY_EXTRAS_STATE = "EXTRAS_STATE"
    private val KEY_EXTRAS_POSITION = "EXTRAS_POSITION"


    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(KEY_EXTRAS_STATE, superState)
        bundle.putInt(KEY_EXTRAS_POSITION, bannerPage.currentItem)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as? Bundle ?: return
        super.onRestoreInstanceState(
            BundleCompat.getParcelable<Parcelable>(
                bundle, KEY_EXTRAS_STATE, Parcelable::class.java
            )
        )
        val position = bundle.getInt(KEY_EXTRAS_POSITION, INVALID)
        if (position >= ZERO && ::bannerPage.isInitialized) bannerPage.setCurrentItem(
            position, false
        )
    }

    override fun setClipChildren(clipChildren: Boolean) {
        if (this.clipChildren == clipChildren) return
        super.setClipChildren(clipChildren)
        this.clipChildren = clipChildren
        if (::bannerPage.isInitialized) {
            bannerPage.clipChildren = clipChildren
            bannerPage.clipToPadding = clipChildren
        }
    }

    fun startPlay(interval: Long = this.interval) {
        if (autoPlay && interval > ZERO) postDelayed(playRunnable, interval)
    }

    /*Stop play*/
    fun stopPlay() {
        if (autoPlay) removeCallbacks(playRunnable)
    }

    fun setCurrentItem(position: Int, smoothScroll: Boolean = true) {
        bannerPage.setCurrentItem(position, smoothScroll)
    }

    fun setPageTransformer(transformer: PageTransformer?) {
        bannerPage.setPageTransformer(transformer)
    }

    fun setItemCount(count: Int) {
        bannerIndicator.setItemCount(count)
    }

    private fun createBannerView(): ViewPager2 {
        return ViewPager2(context, attrs).apply {
            id = R.id.banner_pager
            orientation = this@BannerView.orientation
            offscreenPageLimit = this@BannerView.limit
            isUserInputEnabled = this@BannerView.userInputEnabled
            clipChildren = this@BannerView.clipChildren
            clipToPadding = this@BannerView.clipChildren
        }.also {
            addView(it, BANNER_INDEX, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
    }

    private fun createIndicatorView(): IndicatorView {
        return IndicatorView(context, attrs).apply {
            id = R.id.banner_indicator
            clipChildren = this@BannerView.clipChildren
            clipToPadding = this@BannerView.clipChildren
            setOrientation(this@BannerView.indicatorOrientation)
        }.also {
            addView(it, INDICATOR_INDEX, LayoutParams(indicatorWidth, indicatorHeight).apply {
                gravity = indicatorGravity
            })
        }
    }

    private fun getRealItemCount() = bannerAdapter?.getRealItemCount() ?: 0


    private fun getCyclicItemCount(): Int {
        val count = bannerAdapter?.getRealItemCount() ?: ZERO
        return if (count <= ZERO) ZERO else Int.MAX_VALUE
    }

    private fun getStartPage(position: Int = 0): Int {
        val count = getRealItemCount()
        if (!cyclic || count <= 1) return position
        return Int.MAX_VALUE / count / 2 * count + position % count
    }

    private inner class AdapterWrapper<VH : ViewHolder>(private val adapter: RecyclerView.Adapter<VH>) :
        RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(paren: ViewGroup, type: Int): VH {
            return adapter.onCreateViewHolder(paren, type)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            adapter.onBindViewHolder(holder, position % getRealItemCount())
        }

        override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any?>) {
            adapter.onBindViewHolder(holder, position % getRealItemCount(), payloads)
        }

        override fun getItemViewType(position: Int): Int {
            return adapter.getItemViewType(position % getRealItemCount())
        }

        override fun getItemCount(): Int {
            return getCyclicItemCount()
        }

        fun getRealItemCount(): Int {
            return adapter.itemCount
        }
    }
}