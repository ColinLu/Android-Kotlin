package com.colin.android.demo.kotlin.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.FragmentAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.FragmentHomeBinding
import com.colin.android.demo.kotlin.ui.list.ListFragment
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : AppFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            val array = resources.getStringArray(R.array.flow_data)
            val list: MutableList<Fragment> = mutableListOf()
            array.forEachIndexed { index, test ->
                list.add(ListFragment.newInstance(index, test))
            }
            page.apply {
                adapter = FragmentAdapter(this@HomeFragment, list)
            }
            TabLayoutMediator(tabLayout, page, true, true) { tab, position ->
                tab.text = array[position]
            }.attach()
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {


    }

}