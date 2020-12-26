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
    lateinit var hideHomeSwitch: Switch
    lateinit var hideLastUsedAppsSwitch: Switch

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
        homeIconsSwitch.isChecked = SettingsService.homeIcons()
        homeIconsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeIconsOnHomeSettingIntent.create(baseContext, isChecked))
        }

        hideIconsSwitch = findViewById(R.id.settingsHideIconsSwitch)
        hideIconsSwitch.isChecked = SettingsService.iconsHidden()
        hideIconsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideIconsSettingIntent.create(baseContext, isChecked))
        }

        hideNotificationsSwitch = findViewById(R.id.settingsHideNotificationsSwitch)
        hideNotificationsSwitch.isChecked = SettingsService.notificationsHidden()
        hideNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideNotificationsSettingIntent.create(baseContext, isChecked))
        }

        hideHomeSwitch = findViewById(R.id.settingsHideHomeSwitch)
        hideHomeSwitch.isChecked = SettingsService.homeHidden()
        hideHomeSwitch.setOnCheckedChangeListener { _, isChecked ->
            startService(ChangeHideHomeSettingIntent.create(baseContext, isChecked))
        }

        hideLastUsedAppsSwitch = findViewById(R.id.settingsHideLastUsedApps)
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

        val recyclerView = findViewById<RecyclerView>(R.id.hiddenAppsList)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = SettingsAppListAdapter(hiddenApps, object : SettingsAppOnClickListener {
            override fun onClick(id: Int) {
                val intent = MarkAppAsVisibleIntent.create(baseContext, id)
                startService(intent)
            }
        })

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
    }

    fun setActivityProperties() {
        // Hide top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

}