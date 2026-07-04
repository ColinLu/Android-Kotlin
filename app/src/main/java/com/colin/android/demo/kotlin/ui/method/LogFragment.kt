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
class LogFragment : AppFragment<LayoutRefreshListBinding, ModuleViewModel>() {

    val JSON: String =
        "{\n" + "  \"sites\": {\n" + "    \"site\": [\n" + "      {\n" + "        \"id\": \"1\",\n" + "        \"name\": \"菜鸟教程\",\n" + "        \"url\": \"www.runoob.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"2\",\n" + "        \"name\": \"菜鸟工具\",\n" + "        \"url\": \"www.jyshare.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"3\",\n" + "        \"name\": \"Google\",\n" + "        \"url\": \"www.google.com\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}"

    val XML: String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<sites>\n" + "    <site>\n" + "        <id>1</id>\n" + "        <name>菜鸟教程</name>\n" + "        <url>www.runoob.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>2</id>\n" + "        <name>菜鸟工具</name>\n" + "        <url>www.jyshare.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>3</id>\n" + "        <name>Google</name>\n" + "        <url>www.google.com</url>\n" + "    </site>\n" + "</sites>\n"

    val adapter by lazy { StringAdapter() }
    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@LogFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
            adapter.onItemClickListener = { _, item, position ->
                when (item) {
                    "Log V" -> Log.v(item)
                    "Log V with Tag" -> Log.v(item, TAG)
                    "Log D" -> Log.d(item)
                    "Log D with Tag" -> Log.d(item, TAG)
                    "Log I" -> Log.i(item)
                    "Log I with Tag" -> Log.i(item, TAG)
                    "Log W" -> Log.w(item)
                    "Log W with Tag" -> Log.w(item, TAG)
                    "Log Json" -> Log.json(json = JSON)
                    "Log Json with Tag" -> Log.json(JSON, TAG)
                    "Log Xml" -> Log.xml(xml = XML)
                    "Log Xml with Tag" -> Log.xml(XML, TAG)
                    "Log Error" -> Log.log(Throwable("is error"))
                    else -> Log.log(item)
                }
            }
        }
    }

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
            viewModel.loadData(R.array.log_list)
        }
    }
}