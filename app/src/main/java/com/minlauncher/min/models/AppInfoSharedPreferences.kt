package com.minlauncher.min.models

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minlauncher.min.Constants

class AppInfoSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun load(): List<AppInfo> {
        val appsSerialized = sharedPreferences.getString(Constants.SHARED_PREFERENCES_APPS_KEY.value, "[]")
        val gson = Gson()

        val typeToken = TypeToken.getParameterized(MutableList::class.java, AppInfo::class.java)
        return gson.fromJson(appsSerialized, typeToken.type)
    }

    fun save(apps: List<AppInfo>) {
        val gson = Gson()

        val typeToken = TypeToken.getParameterized(MutableList::class.java, AppInfo::class.java)
        val json = gson.toJson(apps, typeToken.type)
        sharedPreferences.edit {
            putString(Constants.SHARED_PREFERENCES_APPS_KEY.value, json)
            commit()
        }
    }

    fun getHomeApps(): List<AppInfo> {
        return load().filter {
            it.home
        }
    }

    fun getHiddenApps(): List<AppInfo> {
        return load().filter {
            it.hidden
        }
    }

    fun getApps(): List<AppInfo> {
        return load().filter { !it.hidden }
    }

    fun getLastUsed(): List<AppInfo> {
        return load()
            .filter { it.lastUse != null }
            .sortedByDescending { it.lastUse }
            .take(5)
    }

    fun refreshApps(list: List<AppInfo>) {
        val cached = load()
        val refreshedList = list.map {
            val find = cached.filter { info ->
                info.label == it.label && info.packageName == it.packageName
            }

            if (find.any()) {
                val itemInCache = find.first()
                AppInfo(it.label, it.packageName, itemInCache.home, itemInCache.hidden, itemInCache.lastUse)
            } else {
                it
            }
        }

        save(refreshedList)
    }

    fun update(appInfo: AppInfo) {
        val updatedList = load().map {
            if (it.label == appInfo.label && it.packageName == appInfo.packageName) {
                appInfo
            } else {
                it
            }
        }

        save(updatedList)
    }

}