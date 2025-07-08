package com.colin.android.demo.kotlin.ui

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppActivity
import com.colin.android.demo.kotlin.databinding.ActivityMainBinding
import com.colin.library.android.utils.Log
import com.colin.library.android.utils.ToastUtil
import com.colin.library.android.utils.ext.dp
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    var last = 0L
    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewBinding.drawerLayout.isOpen) return
            Log.e("handleOnBackPressed:${onSupportNavigateUp()}")
            if (last <= 0L || System.currentTimeMillis() - last > 1000) {
                ToastUtil.show("再次点击退出应用")
            } else {
                finish()
                exitProcess(0)
            }
            last = System.currentTimeMillis()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        var loading = true
        lifecycleScope.launch {
            viewModel.loading(true)
            delay(1000L)
            loading = false
        }
        splash.setKeepOnScreenCondition {
            Log.e("init.....")
            loading
        }
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setSupportActionBar(viewBinding.appBarMain.toolbar)
        viewBinding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action") {
                    ToastUtil.show(R.string.app_name)
                }.setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = viewBinding.drawerLayout
        val navView: NavigationView = viewBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_home,
                R.id.fragment_view,
                R.id.fragment_method,
                R.id.fragment_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.menuState.collect {
                    Log.i("menuState:$it")
                    it.forEach { id, visible -> getMenu()?.findItem(id)?.setVisible(visible) }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_language) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun update(title: CharSequence?) {
        viewBinding.appBarMain.toolbar.title = title
    }

    fun getMenu(): Menu? = viewBinding.appBarMain.toolbar.menu

    fun getSearchView() = getMenu()?.findItem(R.id.action_search)?.actionView as? SearchView

    fun setMenuVisible(id: Int = R.id.action_search, visible: Boolean = true) {
        viewModel.updateMenu(id, visible)
    }


    private fun createPopupWindow(): ListPopupWindow {
        return ListPopupWindow(this).apply {
            setAdapter(
                ArrayAdapter(
                    this@MainActivity,
                    R.layout.item_text,
                    resources.getStringArray(R.array.local_list)
                ).apply {
                    setOnItemClickListener { _, _, position, _ ->
                        selectedLanguage(position)
                    }
                })
            anchorView = viewBinding.appBarMain.toolbar
            width = 150.dp()
            height = WindowManager.LayoutParams.WRAP_CONTENT
            setDropDownGravity(Gravity.BOTTOM or Gravity.END)
        }
    }

    private fun selectedLanguage(position: Int) {
        val languages = resources.getStringArray(R.array.local_language_list)
        val countries = resources.getStringArray(R.array.local_country_list)
        Log.e("languages:$languages countries:$countries")
        //  onCreate(null)
    }


}