package com.colin.android.demo.kotlin.ui.list

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.ItemAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 21:03
 *
 * Des   :ListFragment
 */
class ListFragment : AppFragment<LayoutRefreshListBinding, ListViewModel>() {
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.apply {
                setColorSchemeResources(R.color.purple_200, R.color.purple_500, R.color.purple_700)
                setOnRefreshListener { loadData(true) }
            }
            list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = ItemAdapter().apply {
                    empty = R.layout.layout_empty
                }
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewModel.apply {
            list.observe {
                Log.i("list observe :$it")
                (viewBinding.list.adapter as ItemAdapter).submitList(it)
                loadStatus(false)
            }
            refresh.observe {
                Log.i("refresh observe:$it")
                viewBinding.refresh.isRefreshing = it
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.loadStatus(true)
            viewModel.loadData()
        }
    }

    companion object {
        private const val EXTRAS_ID = "id"
        private const val EXTRAS_TITLE = "title"

        @JvmStatic
        fun newInstance(id: Int, title: CharSequence): ListFragment {
            val args = Bundle().apply {
                putInt(EXTRAS_ID, id)
                putCharSequence(EXTRAS_TITLE, title)
            }
            val fragment = ListFragment()
            fragment.arguments = args
            return fragment
        }
    }


}