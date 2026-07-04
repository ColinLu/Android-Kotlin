package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.library.android.utils.Log
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :Log日志打印
 */
class PathFragment : AppFragment<LayoutRefreshListBinding, ModuleViewModel>() {

    val adapter by lazy { StringAdapter() }
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@PathFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, position ->
                when (item) {
//                    "can write" -> //TODO
//                    "has sd card" -> //TODO
//                    "can write" -> //TODO
//                    "can write" -> //TODO
//                    "Log V with Tag" -> Log.v(item, TAG)
//                    "Log D" -> Log.d(item)
//                    "Log D with Tag" -> Log.d(item, TAG)
//                    "Log I" -> Log.i(item)
//                    "Log I with Tag" -> Log.i(item, TAG)
//                    "Log W" -> Log.w(item)
//                    "Log W with Tag" -> Log.w(item, TAG)
//                    "Log Json" -> Log.json(json = JSON)
//                    "Log Json with Tag" -> Log.json(JSON, TAG)
//                    "Log Xml" -> Log.xml(xml = XML)
//                    "Log Xml with Tag" -> Log.xml(XML, TAG)
//                    "Log Error" -> Log.log(Throwable("is error"))
//                    else -> Log.log(item)
                }
            }
        }
    }
//    <item>has sd card</item>
//    <item>root system</item>
//    <item>root data</item>
//    <item>user data</item>
//    <item>user cache</item>
//    <item>user code cache</item>
//    <item>internal files</item>
//    <item>internal data</item>
//    <item>internal cache</item>
//    <item>internal shared prefs</item>
//    <item>internal databases</item>
//    <item>internal code cache</item>
//    <item>internal append</item>
//    <item>internal private</item>
//    <item>external code cache</item>
//    <item>external empty</item>
//    <item>external music</item>
//    <item>external podcasts</item>
//    <item>external alarms</item>
//    <item>external notifications</item>
//    <item>external pictures</item>
//    <item>external movies</item>
//    <item>external download</item>
//    <item>external dcim</item>
//    <item>external documents</item>
//    <item>external public empty</item>
//    <item>external public music</item>
//    <item>external public podcasts</item>
//    <item>external public alarms</item>
//    <item>external public notifications</item>
//    <item>external public pictures</item>
//    <item>external public movies</item>
//    <item>external public download</item>
//    <item>external public dcim</item>
//    <item>external public documents</item>
//    </string-array>
    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.list.flowWithLifecycle(lifecycle).collect {
                Log.i(it)
                it.apply { adapter.submitList(it) }
                viewBinding.refresh.isRefreshing = false
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadData(R.array.path_list)
        }
    }
}