package com.colin.android.demo.kotlin.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 21:43
 *
 * Des   :FragmentAdapter
 */
class FragmentAdapter(fragment: Fragment, val list: List<Fragment>) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.lifecycle) {
    override fun getItemCount() = list.size

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}