package com.colin.android.demo.kotlin.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.colin.android.demo.kotlin.IAidlRemoteCallback
import com.colin.android.demo.kotlin.IDemoAidlInterface
import com.colin.android.demo.kotlin.R
import com.colin.android.demo.kotlin.app.AppActivity
import com.colin.android.demo.kotlin.databinding.ActivityMainBinding
import com.colin.android.demo.kotlin.def.ItemBean
import com.colin.library.android.utils.Log
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppActivity<ActivityMainBinding, MainViewModel>() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val callback = object : IAidlRemoteCallback.Stub() {

        override fun aidlChanged(data: String?) {
            data?.let { Log.i("data:$it") }
        }

        override fun itemChanged(itembean: ItemBean?) {
            itembean?.let { Log.i("itemBean:$it") }
        }

    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            aidlService = IDemoAidlInterface.Stub.asInterface(service)
            aidlService!!.register(callback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    private var aidlService: IDemoAidlInterface? = null
    override fun onResume() {
        super.onResume()
        viewModel.update(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.update(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        aidlService?.let {
            it.unregister(callback)
            unbindService(connection)
        }
    }

    override fun initView(bundle: Bundle?, savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setSupportActionBar(viewBinding.appBarMain.toolbar)
        viewBinding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action") {

                }.setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = viewBinding.drawerLayout
        val navView: NavigationView = viewBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.fragment_home, R.id.fragment_gallery, R.id.fragment_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val intent = Intent().apply {
            action = "com.colin.android.demo.kotlin.service.DemoAidlService"
            setComponent(
                ComponentName.createRelative(
                    "com.colin.android.demo.kotlin", ".service.DemoAidlService"
                )
            )
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun initData(bundle: Bundle?, savedInstanceState: Bundle?) {
        viewModel.status.observe {
            Log.i(TAG, "status:$it")
        }
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