package com.colin.library.android.widget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.os.BundleCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.colin.library.android.utils.INVALID
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ZERO
import com.colin.library.android.widget.R
import kotlinx.coroutines.Runnable
import kotlin.math.abs

/**
 * BannerViewPager
 */
@SuppressLint("CutPasteId")
class BannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), DefaultLifecycleObserver {
    private lateinit var banner: ViewPager2

    companion object {
        private val CYCLIC_COUNT = 2
        private val LIMIT_COUNT = 2
        private val INTERVAL_TIME = 3000L
        private val BANNER_INDEX = 0 //固定位置
        private val INDICATOR_INDEX = 1 //固定位置
    }

    var cyclic = true
        set(value) {
            if (field != value) field = value
        }

    /*必须是2的倍数*/
    var cyclicCount = CYCLIC_COUNT
        set(value) {
            if (field == value) return
            field = value
        }

    var limit = LIMIT_COUNT
        set(value) {
            if (field == value) return
            field = value
            if (::banner.isInitialized) banner.setCurrentItem(value)
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

    var userInputEnabled = false
        set(value) {
            if (field == value) return
            field = value
            if (::banner.isInitialized) banner.isUserInputEnabled = value
        }

    var orientation = ViewPager2.ORIENTATION_HORIZONTAL
        set(value) {
            if (field == value) return
            field = value
            if (::banner.isInitialized) banner.orientation = value
        }
    val pageChangeListener: OnPageChangeCallback? = null

    private var bannerAdapter: AdapterWrapper<ViewHolder>? = null
    private var disallowInterceptTouchEvent = true
    private var mRadiusRectF: RectF? = null
    private var mRadiusPath: Path? = null

    private var downX = 0F
    private var downY = 0F


    private val touchSlop by lazy { ViewConfiguration.get(context).scaledTouchSlop / 2 }
    private val pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
        private var scroll = false
        override fun onPageScrolled(position: Int, offset: Float, pixels: Int) {
            val real = getRealPosition(position)
            Log.i("Scrolled-->>current:${banner.currentItem} position:$position cyclic:$cyclic real:$real")
            pageChangeListener?.onPageScrolled(real, offset, pixels)
        }

        override fun onPageSelected(position: Int) {
            val real = getRealPosition(position)
            Log.i("Selected-->>current:${banner.currentItem} position:$position cyclic:$cyclic real:$real scroll:$scroll")
            if (scroll) {
                val real = getRealPosition(position)
                pageChangeListener?.onPageSelected(real)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            Log.i("StateChanged-->>current:${banner.currentItem} cyclic:$cyclic state:$state")
            scroll = state != ViewPager2.SCROLL_STATE_IDLE
            val real = getRealItemCount()
            //无限循环且cyclicCount>0
            if (state == ViewPager2.SCROLL_STATE_IDLE && real > 1 && cyclic && cyclicCount > 0) {
                Log.w("StateChanged--update>>current:${banner.currentItem} real:$real ")
                val current = banner.currentItem
                //最右边
                if (current + 1 == real + cyclicCount) {
                    val next = (current + 1) / real
                    banner.setCurrentItem(next, false)
                }
                //最左边
                if (current == 0) banner.setCurrentItem(real - 1, false)

            }
            pageChangeListener?.onPageScrollStateChanged(state)
        }
    }
    private val playRunnable = object : Runnable {
        override fun run() {
            val count = getRealItemCount()
            Log.i("play interval:$interval count:$count current:${banner.currentItem}")
            if (count > 1) {
                //边界已在onPageScrollStateChanged处理完了，所以不用担心数组越界
                banner.setCurrentItem(banner.currentItem + 1, true)
            }
            if (interval > 0) postDelayed(this, interval)
        }
    }

    init {
        //init attrs
        context.withStyledAttributes(attrs, R.styleable.BannerView, 0, 0) {
            cyclic = getBoolean(R.styleable.BannerView_cyclic, cyclic)
            cyclicCount = getInteger(R.styleable.BannerView_cyclicCount, cyclicCount)
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
        }
        //init view
        banner = findViewById(R.id.banner_pager) as? ViewPager2 ?: createBannerView()
        banner.offscreenPageLimit = limit
        banner.registerOnPageChangeCallback(pageChangeCallback)
    }

    /*cyclicCount>0之后，真正的position 就会偏移*/
    private fun getRealPosition(position: Int): Int {
        val realCount = getRealItemCount()
        if (!cyclic || realCount <= 1 || cyclicCount <= 0) return position
        val half = cyclicCount / 2
        return if (position == 0) realCount - half
        else if (position == realCount + half) 0
        else position - half
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
        val doNotNeedIntercept = banner.isUserInputEnabled != true || getItemCount() <= 1
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
        return if (banner.orientation == ViewPager2.ORIENTATION_VERTICAL) {
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
        banner.adapter = bannerAdapter
        if (bannerAdapter == null) stopPlay()
        else {
            //初始化可能需要跳转到指定位置
            banner.setCurrentItem(getRealPosition(ZERO), false)
            startPlay()
        }
    }

    fun getItemCount() = bannerAdapter?.itemCount ?: 0

    fun getRealItemCount() = bannerAdapter?.getRealItemCount() ?: 0


    private val KEY_EXTRAS_STATE = "EXTRAS_STATE"
    private val KEY_EXTRAS_POSITION = "EXTRAS_POSITION"


    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(KEY_EXTRAS_STATE, superState)
        bundle.putInt(KEY_EXTRAS_POSITION, banner.currentItem)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as? Bundle ?: return
        super.onRestoreInstanceState(
            BundleCompat.getParcelable<Parcelable>(
                bundle,
                KEY_EXTRAS_STATE,
                Parcelable::class.java
            )
        )
        val position = bundle.getInt(KEY_EXTRAS_POSITION, INVALID)
        if (position >= ZERO && ::banner.isInitialized) banner.setCurrentItem(position, false)
    }

    override fun setClipChildren(clipChildren: Boolean) {
        if (this.clipChildren == clipChildren) return
        super.setClipChildren(clipChildren)
        this.clipChildren = clipChildren
        if (::banner.isInitialized) {
            banner.clipChildren = clipChildren
            banner.clipToPadding = clipChildren
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
        banner.setCurrentItem(getRealPosition(position), smoothScroll)
    }

    private fun createBannerView(): ViewPager2 {
        return ViewPager2(context).apply {
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

    private inner class AdapterWrapper<VH : ViewHolder>(private val adapter: RecyclerView.Adapter<VH>) :
        RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(paren: ViewGroup, type: Int): VH {
            return adapter.onCreateViewHolder(paren, type)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            adapter.onBindViewHolder(holder, getAdapterPosition(position))
        }

        override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any?>) {
            adapter.onBindViewHolder(holder, getAdapterPosition(position), payloads)
        }

        override fun getItemViewType(position: Int): Int {
            return adapter.getItemViewType(getAdapterPosition(position))
        }

        override fun getItemCount(): Int {
            return if (!cyclic) adapter.itemCount else adapter.itemCount + cyclicCount
        }

        fun getRealItemCount(): Int {
            return adapter.itemCount
        }

        private fun getAdapterPosition(position: Int): Int {
            val real = getRealItemCount()
            return if (real > ZERO) position % real else position
        }
    }
}