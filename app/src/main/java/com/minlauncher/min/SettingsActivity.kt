package com.minlauncher.min

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.adapters.SettingsAppListAdapter
import com.minlauncher.min.adapters.HiddenAppOnClickListener
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.models.AppInfoSharedPreferences
import com.minlauncher.min.models.SettingsAppListItem


class SettingsActivity : AppCompatActivity() {

    var appInfoSharedPreferences: AppInfoSharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityProperties()
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_APPS.value, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            appInfoSharedPreferences = AppInfoSharedPreferences(sharedPreferences)
        }

        setAdapter()

    }

    private fun setAdapter() {
        appInfoSharedPreferences?.getHiddenApps()?.map {
            val applicationIcon = packageManager.getApplicationIcon(it.packageName)
            SettingsAppListItem(it.label, it.packageName, applicationIcon)
        }?.let {
            val recyclerView = findViewById<RecyclerView>(R.id.hiddenAppsList)
            val onClickListener = object : HiddenAppOnClickListener {
                override fun onClick(position: Int) {

                }
            }

            recyclerView.adapter = SettingsAppListAdapter(it, onClickListener)
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

        appInfoSharedPreferences?.getHomeApps()?.map {
            val applicationIcon = packageManager.getApplicationIcon(it.packageName)
            SettingsAppListItem(it.label, it.packageName, applicationIcon)
        }?.let {
            val homeAppsRecyclerView = findViewById<RecyclerView>(R.id.settingsHomeAppsList)
            val onClickListener = object : HiddenAppOnClickListener {
                override fun onClick(position: Int) {

                }
            }

            homeAppsRecyclerView.adapter = SettingsAppListAdapter(it, onClickListener)
            homeAppsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    fun setActivityProperties() {
        // Hide top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


}