package com.minlauncher.min.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import androidx.core.content.edit
import com.minlauncher.min.Constants
import com.minlauncher.min.Settings
import com.minlauncher.min.intents.*

class SettingsService : Service() {

    companion object {

        private lateinit var sharedPreferences: SharedPreferences

        fun ready(): Boolean {
            return this::sharedPreferences.isInitialized
        }

        fun iconsOnHome(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.ICONS_ON_HOME_SCREEN.NAME), false)
        }

        fun hideIcons(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.HIDE_ICONS.NAME), false)
        }

        fun hideNotifications(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.HIDE_NOTIFICATIONS.NAME), false)
        }

        private fun getKey(key: String): String {
            return Constants.SHARED_PREFERENCES_SETTINGS_KEY_PREFIX.VALUE + "_" + key
        }

    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_SETTINGS.VALUE, Context.MODE_PRIVATE)

        sendBroadcast(IconsOnHomeSettingChangedIntent.create(baseContext, iconsOnHome()))
        sendBroadcast(IconsHideSettingChangedIntent.create(baseContext, hideIcons()))
        sendBroadcast(IconsHideSettingChangedIntent.create(baseContext, hideNotifications()))
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also {
            val action = intent.getStringExtra("action")
            when (action) {
                ChangeIconsOnHomeSettingIntent.ACTION -> {
                    val value = intent.getBooleanExtra(ChangeIconsOnHomeSettingIntent.EXTRA_KEY, false)
                    setBoolean(Settings.ICONS_ON_HOME_SCREEN.NAME, value)
                    sendBroadcast(IconsOnHomeSettingChangedIntent.create(baseContext, value))
                }
                ChangeHideIconsSettingIntent.ACTION -> {
                    val value = intent.getBooleanExtra(ChangeHideIconsSettingIntent.EXTRA_KEY, false)
                    setBoolean(Settings.HIDE_ICONS.NAME, value)
                    sendBroadcast(IconsHideSettingChangedIntent.create(baseContext, value))
                }
                ChangeHideNotificationsSettingIntent.ACTION -> {
                    val value = intent.getBooleanExtra(ChangeHideNotificationsSettingIntent.EXTRA_KEY, false)
                    setBoolean(Settings.HIDE_NOTIFICATIONS.NAME, value)
                    sendBroadcast(IconsHideSettingChangedIntent.create(baseContext, value))
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit {
            val prefixedKey = getKey(key)
            putBoolean(prefixedKey, value)
            commit()
        }
    }
}