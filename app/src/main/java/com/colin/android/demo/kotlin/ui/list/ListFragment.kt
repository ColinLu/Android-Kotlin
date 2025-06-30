package com.colin.android.demo.kotlin.ui.list

import android.Manifest
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ArrayRes
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.adapter.ItemAdapter
import com.colin.android.demo.kotlin.app.AppFragment
import com.colin.android.demo.kotlin.databinding.LayoutRefreshListBinding
import com.colin.android.demo.kotlin.dialog.DialogTips
import com.colin.library.android.utils.INVALID
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.PathUtil
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.widget.recycler.SpaceItemDecoration
import java.io.File

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-12-12 21:03
 *
 * Des   :ListFragment
 */
class ListFragment : AppFragment<LayoutRefreshListBinding, ListViewModel>() {
    private lateinit var adapter: ItemAdapter
    private var id: Int = INVALID

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    val launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            Log.d("requestPermission granted:${result}")
//            if (result) dialog(title, PathUtil.getExternalFile(type))
        }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewBinding.apply {
            adapter = ItemAdapter().apply {
                empty = R.layout.layout_empty
            }
            list.apply {
                this.adapter = this@ListFragment.adapter
                this.addItemDecoration(SpaceItemDecoration(space = 5))
            }
        }
        adapter.onItemClickListener = { _, item, _ ->
            when (item.title) {
                "can write" -> ToastUtil.show("${PathUtil.canWrite()}")
                "has sd card" -> ToastUtil.show("${PathUtil.hasSDCard()}")
                "root system" -> dialog(item.title, PathUtil.getRootSystem())
                "root data" -> dialog(item.title, PathUtil.getRootData())
                "internal data" -> dialog(item.title, PathUtil.getInternalData())
                "internal cache" -> dialog(item.title, PathUtil.getInternalCache())
                "internal files" -> dialog(item.title, PathUtil.getInternalFiles())
                "internal code" -> dialog(item.title, PathUtil.getInternalCode())
                "internal db" -> dialog(item.title, PathUtil.getInternalDatabase())
                "internal shared prefs" -> dialog(item.title, PathUtil.getInternalSp())
                "app external empty" -> dialog(item.title, PathUtil.getAppExternalFile(type = null))
                "app external music" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_PICTURES)
                )

                "app external podcasts" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_PODCASTS)
                )

                "app external alarms" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_ALARMS)
                )

                "app external notifications" -> dialog(
                    item.title,
                    PathUtil.getAppExternalFile(type = Environment.DIRECTORY_NOTIFICATIONS)
                )

                "app external pictures" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_PICTURES)
                )

                "app external movies" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_MOVIES)
                )

                "app external download" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_DOWNLOADS)
                )

                "app external dcim" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_DCIM)
                )

                "app external documents" -> dialog(
                    item.title, PathUtil.getAppExternalFile(type = Environment.DIRECTORY_DOCUMENTS)
                )

                "external cache" -> dialog(item.title, PathUtil.getExternalCache())
                "external download cache" -> dialog(item.title, PathUtil.getDownloadCache())
                "external empty" -> dialog(item.title, PathUtil.getStorageExternalFile(type = ""))
                "external music" -> dialog(
                    item.title,
                    PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_PICTURES)
                )

                "external podcasts" -> dialog(
                    item.title,
                    PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_PODCASTS)
                )

                "external alarms" -> dialog(
                    item.title, PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_ALARMS)
                )

                "external notifications" -> dialog(
                    item.title,
                    PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_NOTIFICATIONS)
                )

                "external pictures" -> dialog(
                    item.title,
                    PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_PICTURES)
                )

                "external movies" -> dialog(
                    item.title, PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_MOVIES)
                )

                "external download" -> dialog(
                    item.title,
                    PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_DOWNLOADS)
                )

                "external dcim" -> dialog(
                    item.title, PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_DCIM)
                )

                "external documents" -> dialog(
                    item.title,
                    PathUtil.getStorageExternalFile(type = Environment.DIRECTORY_DOCUMENTS)
                )
            }
        }
    }


    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        id = bundle?.getInt(EXTRAS_ID, INVALID) ?: INVALID
        viewModel.apply {
            list.observe {
                (viewBinding.list.adapter as ItemAdapter).submitList(it)
            }
        }
    }

    override fun loadData(refresh: Boolean) {
        viewModel.loadData(id)
    }

    override fun onDestroyView() {
        adapter.clear()
        super.onDestroyView()
    }

    companion object {
        private const val EXTRAS_ID = "id"
        private const val EXTRAS_TITLE = "title"

        @JvmStatic
        fun newInstance(@ArrayRes id: Int, title: CharSequence): ListFragment {
            val args = Bundle().apply {
                putInt(EXTRAS_ID, id)
                putCharSequence(EXTRAS_TITLE, title)
            }
            val fragment = ListFragment()
            fragment.arguments = args
            return fragment
        }

        @JvmStatic
        fun toNavigate(
            fragment: Fragment, @ArrayRes id: Int = R.array.path_list, title: CharSequence? = null
        ) {
            fragment.findNavController().navigate(
                R.id.action_list, Bundle().apply {
                    putInt(EXTRAS_ID, id)
                    title?.let { putCharSequence(EXTRAS_TITLE, it) }
                })
        }
    }

    private fun dialog(title: CharSequence, file: File?) {
        DialogTips.newInstance(title, "isFile:${file?.isFile}\n${file?.path}").show(this)
    }

}