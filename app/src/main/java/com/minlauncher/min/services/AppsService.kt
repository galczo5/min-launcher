package com.minlauncher.min.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.room.Room
import com.minlauncher.min.db.AppInfoDatabase
import com.minlauncher.min.db.AppInfoEntity
import com.minlauncher.min.db.AppInfoEntityConverter
import com.minlauncher.min.intents.*
import com.minlauncher.min.models.AppInfo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppsService : Service() {

    companion object {
        lateinit var db: AppInfoDatabase
        var apps = listOf<AppInfo>()

        fun allApps(): List<AppInfo> {
            return apps.toList()
        }

        fun homeApps(): List<AppInfo> {
            return apps.filter { it.home }
        }

        fun hiddenApps(): List<AppInfo> {
            return apps.filter { it.hidden }
        }

        fun lastUsed(): List<AppInfo> {
            return apps.sortedByDescending { it.lastUse } .take(5)
        }
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppInfoDatabase::class.java, "min-app-info").build()
        reloadCache()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also {
            val action = it.getStringExtra("action")
            GlobalScope.launch {
                when (action) {
                    ReloadAppsListIntent.ACTION -> {
                        loadList()
                    }
                    MarkAppAsHiddenIntent.ACTION -> {
                        val id = it.getIntExtra(MarkAppAsHiddenIntent.EXTRA, -1)
                        changeVisibility(id, true)
                    }
                    MarkAppAsVisibleIntent.ACTION -> {
                        val id = it.getIntExtra(MarkAppAsVisibleIntent.EXTRA, -1)
                        changeVisibility(id, false)
                    }
                    PinAppIntent.ACTION -> {
                        val id = it.getIntExtra(PinAppIntent.EXTRA, -1)
                        pinApp(id, true)
                    }
                    UnpinAppIntent.ACTION -> {
                        val id = it.getIntExtra(UnpinAppIntent.EXTRA, -1)
                        pinApp(id, false)
                    }
                    SetLastUseDateIntent.ACTION -> {
                        val id = it.getIntExtra(SetLastUseDateIntent.EXTRA, -1)
                        updateLastUse(id)
                    }
                    InitAppsListIntent.ACTION -> {
                        reloadCache()
                    }
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun changeVisibility(id: Int, hidden: Boolean) {
        db.dao().setHidden(id, hidden)
        reloadCache()
        notifyRefresh()
    }

    private suspend fun pinApp(id: Int, home: Boolean) {
        db.dao().setHome(id, home)
        reloadCache()
        notifyRefresh()
    }

    private suspend fun updateLastUse(id: Int) {
        val date = System.currentTimeMillis()
        db.dao().setLastUse(id, date)
        reloadCache()
        notifyRefresh()
    }

    private suspend fun loadList() {
        getInstalledApps()?.also { installedApps ->
            val all = db.dao().all()
            val updatedApps = installedApps.map { appInfoEntity ->
                val find = all.find { it.label == appInfoEntity.label && it.packageName == appInfoEntity.packageName }
                if (find != null) {
                    copyEntity(find)
                } else {
                    appInfoEntity
                }
            }

            db.dao().updateDatabase(updatedApps)
            reloadCache()
            notifyRefresh()
        }
    }

    private fun copyEntity(find: AppInfoEntity): AppInfoEntity {
        return AppInfoEntity(
            0,
            find.label,
            find.packageName,
            find.home,
            find.hidden,
            find.lastUse
        )
    }

    private fun notifyRefresh() {
        val intent = RefreshAppsListIntent.create()
        sendBroadcast(intent)
    }

    private fun getInstalledApps(): List<AppInfoEntity>? {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = packageManager?.queryIntentActivities(intent, 0)
        allApps?.sortBy { resolveInfo ->
            resolveInfo.loadLabel(packageManager).toString().toUpperCase()
        }

        return allApps?.map {
            val label = it.loadLabel(packageManager).toString()
            val packageName = it.activityInfo.packageName
            AppInfoEntity(0, label, packageName, false, false, null)
        }
    }

    private fun reloadCache() {
        GlobalScope.launch {
            apps = AppInfoEntityConverter.toAppInfo(db.dao().all())
        }
    }

}