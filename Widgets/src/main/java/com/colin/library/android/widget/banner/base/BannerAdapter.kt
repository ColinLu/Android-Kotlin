package com.colin.library.android.widget.banner.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.colin.library.android.widget.banner.BannerViewPager
import com.colin.library.android.widget.banner.utils.BannerUtils
import com.colin.library.android.widget.base.BaseViewHolder

class BannerViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
abstract class BannerAdapter<ITEM>(val id: Int) : RecyclerView.Adapter<BannerViewHolder>() {
    private val mList: MutableList<ITEM> = ArrayList()
    private var isCyclic = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val holder = onCreateHolder(parent, viewType)
        holder.itemView.setOnClickListener { clickedView: View? ->
            val adapterPosition = holder.adapterPosition
//            if (mPageClickListener != null && adapterPosition != RecyclerView.NO_POSITION) {
//                val realPosition: Int =
//                    BannerUtils.getRealPosition(holder.adapterPosition, getListSize())
//                mPageClickListener?.onPageClick(clickedView, realPosition)
//            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val realPosition: Int = BannerUtils.getRealPosition(position, getListSize())
        onBindView(holder, mList[realPosition], realPosition, getListSize())
    }

    override fun getItemViewType(position: Int): Int {
        val realPosition: Int = BannerUtils.getRealPosition(position, getListSize())
        return getViewType(realPosition)
    }

    override fun getItemCount(): Int {
        return if (isCyclic && getListSize() > 1) {
            Int.MAX_VALUE
        } else {
            getListSize()
        }
    }

    fun getData(): MutableList<ITEM> {
        return mList
    }

    fun setData(list: List<ITEM>?) {
        if (null != list) {
            mList.clear()
            mList.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun addData(list: List<ITEM>?) {
        list?.let {
            mList.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun setCyclic(cyclic: Boolean) {
        isCyclic = cyclic
    }
//
//    fun setPageClickListener(pageClickListener: BannerViewPager.OnPageClickListener?) {
//        mPageClickListener = pageClickListener
//    }

    fun getListSize(): Int {
        return mList.size
    }

    protected fun getViewType(position: Int): Int {
        return 0
    }

    fun isCyclic(): Boolean {
        return isCyclic
    }


    /**
     * Generally,there is no need to override this method in subclasses.
     *
     * This method called by [.onCreateViewHolder] to create a default [ ]
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     * @return ViewHolder extends [BannerViewHolder].
     */
    abstract fun onCreateHolder(
        parent: ViewGroup, viewType: Int
    ): BannerViewHolder

    /**
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param data Current item data.
     * @param position Current item position.
     * @param pageSize Page size of BVP,equals [BannerAdapter.getListSize].
     */
    abstract fun onBindView(
        holder: BannerViewHolder, data: ITEM, position: Int, pageSize: Int
    )
}