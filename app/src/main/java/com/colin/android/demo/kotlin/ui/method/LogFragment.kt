package com.colin.android.demo.kotlin.ui.method

import android.os.Bundle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.StringAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.createModel
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.library.android.utils.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-09-13
 *
 * Des   :Log日志打印
 */
class LogFragment : AppFragment<LayoutRefreshListBinding, LogViewModel>() {
    private val TAG = "LogFragment"
    override val viewModel: LogViewModel by lazy { createModel(this) }

    val JSON: String =
        "{\n" + "  \"sites\": {\n" + "    \"site\": [\n" + "      {\n" + "        \"id\": \"1\",\n" + "        \"name\": \"菜鸟教程\",\n" + "        \"url\": \"www.runoob.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"2\",\n" + "        \"name\": \"菜鸟工具\",\n" + "        \"url\": \"www.jyshare.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"3\",\n" + "        \"name\": \"Google\",\n" + "        \"url\": \"www.google.com\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}"

    val XML: String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<sites>\n" + "    <site>\n" + "        <id>1</id>\n" + "        <name>菜鸟教程</name>\n" + "        <url>www.runoob.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>2</id>\n" + "        <name>菜鸟工具</name>\n" + "        <url>www.jyshare.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>3</id>\n" + "        <name>Google</name>\n" + "        <url>www.google.com</url>\n" + "    </site>\n" + "</sites>\n"

    val adapter by lazy { StringAdapter() }
    override fun initView(savedInstanceState: Bundle?) {
        viewBinding.apply {
            refresh.setColorSchemeResources(
                R.color.purple_200, R.color.purple_500, R.color.purple_700
            )
            refresh.setOnRefreshListener { loadData(true) }

            layoutList.list.apply {
                this.layoutManager = LinearLayoutManager(requireActivity())
                this.adapter = this@LogFragment.adapter
            }
            adapter.onItemClickListener = { _, item ->
                when (item) {
                    "Log V" -> L.v(item)
                    "Log V with Tag" -> L.v(TAG, item)
                    "Log D" -> L.d(item)
                    "Log D with Tag" -> L.d(TAG, item)
                    "Log I" -> L.i(item)
                    "Log I with Tag" -> L.i(TAG, item)
                    "Log W" -> L.w(item)
                    "Log W with Tag" -> L.w(TAG, item)
                    "Log E" -> L.e(item)
                    "Log E with Tag" -> L.e(TAG, item)
                    "Log A" -> L.a(item)
                    "Log A with Tag" -> L.a(TAG, item)
                    "Log Json" -> L.json(json = JSON)
                    "Log Json with Tag" -> L.json(TAG, JSON)
                    "Log Xml" -> L.xml(xml = XML)
                    "Log Xml with Tag" -> L.xml(TAG, XML)
                    "Log Error" -> L.log(Throwable("is error"))
                    else -> L.log(item)
                }
            }
        }
    }

    override fun initData(bundle: Bundle?) {
        lifecycleScope.launch {
            viewModel.list.flowWithLifecycle(lifecycle).collect {
                L.i("LogFragment", it)
                it.apply { adapter.setData(it) }
                viewBinding.refresh.isRefreshing = false
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.loadData()
        }
    }
}