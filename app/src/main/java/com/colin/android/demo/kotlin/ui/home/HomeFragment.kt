package com.colin.android.demo.kotlin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.BannerAdapter
import com.colin.android.demo.kotlin.adapter.FragmentAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentHomeBinding
import com.colin.android.demo.kotlin.ui.MainViewModel
import com.colin.android.demo.kotlin.ui.list.ListFragment
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.banner.transform.ScaleInTransformer
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : AppFragment<FragmentHomeBinding, MainViewModel>() {
    override fun bindViewModelStore() = requireActivity().viewModelStore
    private var bannerAdapter = BannerAdapter()
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            val array = resources.getStringArray(R.array.flow_data)
            val list: MutableList<Fragment> = mutableListOf()
            array.forEachIndexed { index, test ->
                list.add(ListFragment.newInstance(R.array.flow_data, test))
            }
            page.apply {
                adapter = FragmentAdapter(this@HomeFragment, list)
            }
            TabLayoutMediator(tabLayout, page, true, true) { tab, position ->
                tab.text = array[position]
            }.attach()

            header.setAdapter(bannerAdapter)
            header.setPageTransformer(ScaleInTransformer())
            header.pageChangeListener = object : OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    Log.i("onPageSelected-->>position:$position")
                }
            }
        }
        bannerAdapter.onItemClickListener = { view, item, position ->
            ToastUtil.show("position:$position")
        }
        bannerAdapter.submitList(createBanner())
        viewBinding.header.setItemCount(bannerAdapter.itemCount)

    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {


    }

    private fun createBanner() = listOf(
        R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4, R.mipmap.banner5
    )

    override fun onResume() {
        super.onResume()
        viewModel.updateMenu(R.id.action_language, true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateMenu(R.id.action_language, false)
    }


}