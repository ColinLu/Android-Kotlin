package com.colin.android.demo.kotlin.ui

import android.os.Bundle
import android.view.Menu
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppActivity
import com.colin.android.demo.kotlin.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
//        EdgeToEdgeUtils.applyEdgeToEdge(window, true)
        super.onCreate(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        setSupportActionBar(viewBinding.appBarMain.toolbar)
        viewBinding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).setAnchorView(R.id.fab).show()
        }
    }

    override fun initData(bundle: Bundle?) {
        val drawerLayout: DrawerLayout = viewBinding.drawerLayout
        val navView: NavigationView = viewBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}