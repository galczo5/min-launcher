package com.minlauncher.min.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.minlauncher.min.Constants
import com.minlauncher.min.intents.*
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.models.AppInfoSharedPreferences
import java.util.*

class AppsService : Service() {

    companion object {
        private var apps: List<AppInfo> = listOf()

        fun allApps(): List<AppInfo> {
            return apps.filter { !it.hidden }
        }

        fun homeApps(): List<AppInfo> {
            return allApps().filter { it.home }
        }

        fun hiddenApps(): List<AppInfo> {
            return apps.filter { it.hidden }
        }

        fun lastUsed(): List<AppInfo> {
            return allApps()
                .sortedByDescending { it.lastUse }
                .take(5)
        }
    }

    lateinit var preferences: AppInfoSharedPreferences

    override fun onCreate() {
        super.onCreate()

        val key = Constants.SHARED_PREFERENCES_APPS.value
        val sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
        preferences = AppInfoSharedPreferences(sharedPreferences)

        loadList()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also {
            val action = it.getStringExtra("action")
            when (action) {
                ReloadAppsListIntent.ACTION -> {
                    loadList()
                }
                MarkAppAsHiddenIntent.ACTION -> {
                    val label = it.getStringExtra("label")
                    val packageName = it.getStringExtra("packageName")
                    changeVisibility(label, packageName, true)
                }
                MarkAppAsVisibleIntent.ACTION -> {
                    val label = it.getStringExtra("label")
                    val packageName = it.getStringExtra("packageName")
                    changeVisibility(label, packageName, false)
                }
                PinAppIntent.ACTION -> {
                    val label = it.getStringExtra("label")
                    val packageName = it.getStringExtra("packageName")
                    pinApp(label, packageName, true)
                }
                UnpinAppIntent.ACTION -> {
                    val label = it.getStringExtra("label")
                    val packageName = it.getStringExtra("packageName")
                    pinApp(label, packageName, false)
                }
                SetLastUseDateIntent.ACTION -> {
                    val label = it.getStringExtra("label")
                    val packageName = it.getStringExtra("packageName")
                    updateLastUse(label, packageName)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun changeVisibility(label: String?, packageName: String?, hidden: Boolean) {
        preferences.getApp(label, packageName)?.also { app ->
            preferences.update(AppInfo(app.label, app.packageName, app.home, hidden, app.lastUse))
            loadList()
            notifyRefresh()
        }
    }

    private fun pinApp(label: String?, packageName: String?, home: Boolean) {
        preferences.getApp(label, packageName)?.also { app ->
            preferences.update(AppInfo(app.label, app.packageName, home, app.hidden, app.lastUse))
            loadList()
            notifyRefresh()
        }
    }

    private fun updateLastUse(label: String?, packageName: String?) {
        preferences.getApp(label, packageName)?.also { app ->
            val date = Date(System.currentTimeMillis())
            preferences.update(AppInfo(app.label, app.packageName, app.home, app.hidden, date))
            loadList()
            notifyRefresh()
        }
    }

    private fun loadList() {
        getInstalledApps()?.also {
            preferences.refreshApps(it)
            apps = preferences.load()
            notifyRefresh()
        }
    }

    private fun notifyRefresh() {
        val intent = RefreshAppsListIntent.create()
        sendBroadcast(intent)
    }

    private fun getInstalledApps(): List<AppInfo>? {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = packageManager?.queryIntentActivities(intent, 0)
        allApps?.sortBy { resolveInfo ->
            resolveInfo.loadLabel(packageManager).toString().toUpperCase()
        }

        return allApps?.map {
            val label = it.loadLabel(packageManager).toString()
            val packageName = it.activityInfo.packageName
            AppInfo(label, packageName, false, false, null)
        }
    }
}