package com.minlauncher.min

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.adapters.SettingsAppListAdapter
import com.minlauncher.min.adapters.SettingsAppOnClickListener
import com.minlauncher.min.fragments.SettingsImageSwitch
import com.minlauncher.min.intents.*
import com.minlauncher.min.models.SettingsAppListItem
import com.minlauncher.min.services.AppsService
import com.minlauncher.min.services.SettingsService

class SettingsActivity : AppCompatActivity() {

    private val refreshAppsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setAdapter()
        }
    }

    private val themeDarkFragment = SettingsImageSwitch(R.drawable.ic_min_dark)
    private val themeLightFragment = SettingsImageSwitch(R.drawable.ic_min_light)
    private val homeIconsFragment = SettingsImageSwitch(R.drawable.ic_min_home_icons)
    private val homeListFragment = SettingsImageSwitch(R.drawable.ic_min_home_list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityProperties()
        setContentView(R.layout.activity_settings)

        registerReceiver(refreshAppsReceiver, IntentFilter(RefreshAppsListIntent.ACTION))
        setAdapter()

        themeDarkFragment.isActive(SettingsService.darkMode())
        val themeDarkFrame = findViewById<FrameLayout>(R.id.settingsThemeDark)
        themeDarkFrame.setOnClickListener {
            startService(ChangeDarkModeSettingIntent.create(baseContext, true))
            themeDarkFragment.isActive(true)
            themeLightFragment.isActive(false)
        }

        themeLightFragment.isActive(!SettingsService.darkMode())
        val lightThemeFrame = findViewById<FrameLayout>(R.id.settingsThemeLight)
        lightThemeFrame.setOnClickListener {
            startService(ChangeDarkModeSettingIntent.create(baseContext, false))
            themeDarkFragment.isActive(false)
            themeLightFragment.isActive(true)
        }

        homeIconsFragment.isActive(SettingsService.homeIcons())
        val homeIconsFrame = findViewById<FrameLayout>(R.id.settingsHomeIcons)
        homeIconsFrame.setOnClickListener {
            startService(ChangeIconsOnHomeSettingIntent.create(baseContext, true))
            homeIconsFragment.isActive(true)
            homeListFragment.isActive(false)
        }

        homeListFragment.isActive(!SettingsService.homeIcons())
        val homeListFrame = findViewById<FrameLayout>(R.id.settingsHomeList)
        homeListFrame.setOnClickListener {
            startService(ChangeIconsOnHomeSettingIntent.create(baseContext, false))
            homeIconsFragment.isActive(false)
            homeListFragment.isActive(true)
        }

        supportFragmentManager.beginTransaction().also {
            it.replace(R.id.settingsThemeDark, themeDarkFragment)
            it.replace(R.id.settingsThemeLight, themeLightFragment)

            it.replace(R.id.settingsHomeIcons, homeIconsFragment)
            it.replace(R.id.settingsHomeList, homeListFragment)

            it.commit()
        }

        val hideIconsSwitch = findViewById<Switch>(R.id.settingsHideIconsSwitch)
        hideIconsSwitch.isChecked = SettingsService.iconsHidden()
        hideIconsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideIconsSettingIntent.create(baseContext, isChecked))
        }

        val hideNotificationsSwitch = findViewById<Switch>(R.id.settingsHideNotificationsSwitch)
        hideNotificationsSwitch.isChecked = SettingsService.notificationsHidden()
        hideNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideNotificationsSettingIntent.create(baseContext, isChecked))
        }

        val hideHomeSwitch = findViewById<Switch>(R.id.settingsHideHomeSwitch)
        hideHomeSwitch.isChecked = SettingsService.homeHidden()
        hideHomeSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideHomeSettingIntent.create(baseContext, isChecked))
        }

        val hideLastUsedAppsSwitch = findViewById<Switch>(R.id.settingsHideLastUsedApps)
        hideLastUsedAppsSwitch.isChecked = SettingsService.lastUsedAppsHidden()
        hideLastUsedAppsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideLastUsedAppsSettingIntent.create(baseContext, isChecked))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(refreshAppsReceiver)
    }

    private fun setAdapter() {
         val hiddenApps = AppsService.hiddenApps().map {
            val applicationIcon = packageManager.getApplicationIcon(it.packageName)
            SettingsAppListItem(it.id, it.label, it.packageName, applicationIcon)
        }

        val hiddenAppsRecyclerView = findViewById<RecyclerView>(R.id.hiddenAppsList)
        hiddenAppsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        hiddenAppsRecyclerView.adapter = SettingsAppListAdapter(hiddenApps, object : SettingsAppOnClickListener {
            override fun onClick(id: Int) {
                val intent = MarkAppAsVisibleIntent.create(baseContext, id)
                startService(intent)
            }
        })

        val hiddenAppsTextView = findViewById<TextView>(R.id.settingsHiddenAppsTextView)
        if (hiddenApps.isEmpty()) {
            hiddenAppsTextView.visibility = View.GONE
            hiddenAppsRecyclerView.visibility = View.GONE
        }

        val homeApps = AppsService.homeApps().map {
            val applicationIcon = packageManager.getApplicationIcon(it.packageName)
            SettingsAppListItem(it.id, it.label, it.packageName, applicationIcon)
        }
        val homeAppsRecyclerView = findViewById<RecyclerView>(R.id.settingsHomeAppsList)
        homeAppsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        homeAppsRecyclerView.adapter = SettingsAppListAdapter(homeApps, object : SettingsAppOnClickListener {
            override fun onClick(id: Int) {
                val intent = UnpinAppIntent.create(baseContext, id)
                startService(intent)
            }
        })

        val homeAppsTextView = findViewById<TextView>(R.id.settingsHomeAppsTextView)
        if (homeApps.isEmpty()) {
            homeAppsTextView.visibility = View.GONE
            homeAppsTextView.visibility = View.GONE
        }
    }

    fun setActivityProperties() {
        // Hide top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}