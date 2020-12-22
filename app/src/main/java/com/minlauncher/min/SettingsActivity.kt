package com.minlauncher.min

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.adapters.SettingsAppListAdapter
import com.minlauncher.min.adapters.SettingsAppOnClickListener
import com.minlauncher.min.intents.*
import com.minlauncher.min.models.SettingsAppListItem
import com.minlauncher.min.services.AppsService
import com.minlauncher.min.services.SettingsService

class SettingsActivity : AppCompatActivity() {

    lateinit var homeIconsSwitch: Switch
    lateinit var hideIconsSwitch: Switch
    lateinit var hideNotificationsSwitch: Switch

    private val refreshAppsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setAdapter()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityProperties()
        setContentView(R.layout.activity_settings)

        registerReceiver(refreshAppsReceiver, IntentFilter(RefreshAppsListIntent.ACTION))
        setAdapter()

        homeIconsSwitch = findViewById(R.id.settingsHomeIconsSwitch)
        homeIconsSwitch.isChecked = SettingsService.iconsOnHome()
        homeIconsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeIconsOnHomeSettingIntent.create(baseContext, isChecked))
        }

        hideIconsSwitch = findViewById(R.id.settingsHideIconsSwitch)
        hideIconsSwitch.isChecked = SettingsService.hideIcons()
        hideIconsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideIconsSettingIntent.create(baseContext, isChecked))
        }

        hideNotificationsSwitch = findViewById(R.id.settingsHideNotificationsSwitch)
        hideNotificationsSwitch.isChecked = SettingsService.hideNotifications()
        hideNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideNotificationsSettingIntent.create(baseContext, isChecked))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(refreshAppsReceiver)
    }

    private fun setAdapter() {
        val hiddenApps = AppsService.hiddenApps().map {
            val applicationIcon = packageManager.getApplicationIcon(it.packageName)
            SettingsAppListItem(it.label, it.packageName, applicationIcon)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.hiddenAppsList)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = SettingsAppListAdapter(hiddenApps, object : SettingsAppOnClickListener {
            override fun onClick(label: String, packageName: String) {
                val intent = MarkAppAsVisibleIntent.create(baseContext, label, packageName)
                startService(intent)
            }
        })

        val homeApps = AppsService.homeApps().map {
            val applicationIcon = packageManager.getApplicationIcon(it.packageName)
            SettingsAppListItem(it.label, it.packageName, applicationIcon)
        }

        val homeAppsRecyclerView = findViewById<RecyclerView>(R.id.settingsHomeAppsList)
        homeAppsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        homeAppsRecyclerView.adapter = SettingsAppListAdapter(homeApps, object : SettingsAppOnClickListener {
            override fun onClick(label: String, packageName: String) {
                val intent = UnpinAppIntent.create(baseContext, label, packageName)
                startService(intent)
            }
        })
    }

    fun setActivityProperties() {
        // Hide top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}