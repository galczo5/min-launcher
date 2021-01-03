package com.minlauncher.min

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.edit
import androidx.viewpager2.widget.ViewPager2
import com.minlauncher.min.adapters.MainActivityScreensAdapter
import com.minlauncher.min.intents.*
import com.minlauncher.min.services.AppsService
import com.minlauncher.min.services.SettingsService

class MainActivity : AppCompatActivity() {

    private var paused: Boolean = true
    private var hideNotifications: Boolean = false
    private var hideHome: Boolean = false

    private lateinit var viewPager :ViewPager2
    private lateinit var adapter: MainActivityScreensAdapter

    private val darkModeChangedBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val mode = themeMode()
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private val hideNotificationsBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            hideNotifications = SettingsService.notificationsHidden()
            if (!paused) {
                setViewPager()
            }
        }
    }

    private val hideHomeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            hideHome = SettingsService.homeHidden()
            if (!paused) {
                setViewPager()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActivityProperties()
        setContentView(R.layout.activity_main)

        baseContext?.let { ReloadAppsListIntent.create(it) }.also {
            startService(it)
        }

        registerReceiver(darkModeChangedBroadcastReceiver, IntentFilter(DarkModeSettingChangedIntent.ACTION))
        registerReceiver(hideNotificationsBroadcastReceiver, IntentFilter(NotificationsHideChangedIntent.ACTION))
        registerReceiver(hideHomeBroadcastReceiver, IntentFilter(HomeHideSettingChangedIntent.ACTION))

        val darkModeValue = SettingsService.getInitValue(
            { name, mode -> getSharedPreferences(name, mode) },
            Settings.DARK_MODE.NAME
        )

        AppCompatDelegate.setDefaultNightMode(darkMode(darkModeValue))

        viewPager = findViewById(R.id.viewPager)
        setViewPager()

        startService(Intent(baseContext, AppsService::class.java))
        startService(InitAppsListIntent.create(baseContext))
        startService(Intent(baseContext, SettingsService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(darkModeChangedBroadcastReceiver)
        unregisterReceiver(hideNotificationsBroadcastReceiver)
        unregisterReceiver(hideHomeBroadcastReceiver)
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    override fun onResume() {
        paused = false
        super.onResume()

        if (SettingsService.ready()) {
            hideNotifications = SettingsService.notificationsHidden()
            hideHome = SettingsService.homeHidden()

            viewPager.adapter = null
            adapter.clear()
            setViewPager()
        }
    }

    private fun setActivityProperties() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun setViewPager() {
        adapter = MainActivityScreensAdapter(supportFragmentManager, lifecycle, hideNotifications, hideHome)
        viewPager.adapter = adapter
        viewPager.currentItem = adapter.currentPage
        viewPager.offscreenPageLimit = 100
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val adapterSize = adapter.itemCount
                if (position == 0) {
                    viewPager.currentItem = adapterSize - 2
                } else if (position == adapterSize - 1) {
                    viewPager.currentItem = 1
                }
                super.onPageSelected(position)
            }
        })
    }

    private fun themeMode(): Int {
        return darkMode(SettingsService.darkMode())
    }

    private fun darkMode(value: Boolean): Int {
        return if (value) {
            MODE_NIGHT_YES
        } else {
            MODE_NIGHT_NO
        }
    }
}