package com.minlauncher.min.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import androidx.core.content.edit
import com.minlauncher.min.Settings
import com.minlauncher.min.intents.*

class SettingsService : Service() {

    companion object {

        const val SHARED_PREFERENCES_SETTINGS = "SHARED_PREFERENCES_SETTINGS"
        const val SHARED_PREFERENCES_SETTINGS_KEY_PREFIX = "SHARED_PREFERENCES_SETTINGS_KEY_PREFIX"

        private lateinit var sharedPreferences: SharedPreferences

        fun init(sp: SharedPreferences) {
            sharedPreferences = sp;
        }

        fun getInitValue(getSharedPreferences: (name: String, mode: Int) -> SharedPreferences, key: String): Boolean {
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_SETTINGS, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(getKey(key), false)
        }

        fun ready(): Boolean {
            return this::sharedPreferences.isInitialized
        }

        fun homeIcons(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.ICONS_ON_HOME_SCREEN.NAME), false)
        }

        fun iconsHidden(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.HIDE_ICONS.NAME), false)
        }

        fun notificationsHidden(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.HIDE_NOTIFICATIONS.NAME), false)
        }

        fun homeHidden(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.HIDE_HOME.NAME), false)
        }

        fun lastUsedAppsHidden(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.HIDE_LAST_USED_APPS.NAME), false)
        }

        fun darkMode(): Boolean {
            return sharedPreferences.getBoolean(getKey(Settings.DARK_MODE.NAME), false)
        }

        private fun getKey(key: String): String {
            return SHARED_PREFERENCES_SETTINGS_KEY_PREFIX + "_" + key
        }

    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.also {
            val action = intent.getStringExtra("action")
            when (action) {
                ChangeIconsOnHomeSettingIntent.ACTION -> {
                    saveChanges(
                        it,
                        ChangeIconsOnHomeSettingIntent.EXTRA_KEY,
                        Settings.ICONS_ON_HOME_SCREEN.NAME,
                        IconsOnHomeSettingChangedIntent.create()
                    )
                }
                ChangeHideIconsSettingIntent.ACTION -> {
                    saveChanges(
                        it,
                        ChangeHideIconsSettingIntent.EXTRA_KEY,
                        Settings.HIDE_ICONS.NAME,
                        IconsHideSettingChangedIntent.create()
                    )
                }
                ChangeHideNotificationsSettingIntent.ACTION -> {
                    saveChanges(
                        it,
                        ChangeHideNotificationsSettingIntent.EXTRA_KEY,
                        Settings.HIDE_NOTIFICATIONS.NAME,
                        IconsHideSettingChangedIntent.create()
                    )
                }
                ChangeHideHomeSettingIntent.ACTION -> {
                    saveChanges(
                        it,
                        ChangeHideHomeSettingIntent.EXTRA_KEY,
                        Settings.HIDE_HOME.NAME,
                        HomeHideSettingChangedIntent.create()
                    )
                }
                ChangeHideLastUsedAppsSettingIntent.ACTION -> {
                    saveChanges(
                        it,
                        ChangeHideLastUsedAppsSettingIntent.EXTRA_KEY,
                        Settings.HIDE_LAST_USED_APPS.NAME,
                        LastUsedAppsHideSettingChangedIntent.create()
                    )
                }
                ChangeDarkModeSettingIntent.ACTION -> {
                    saveChanges(
                        it,
                        ChangeDarkModeSettingIntent.EXTRA_KEY,
                        Settings.DARK_MODE.NAME,
                        DarkModeSettingChangedIntent.create()
                    )
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun saveChanges(intent: Intent, extraKey: String, settingKey: String, intentToBroadcast: Intent) {
        val value = intent.getBooleanExtra(extraKey, false)
        setBoolean(settingKey, value)
        sendBroadcast(intentToBroadcast)
    }

    private fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit {
            val prefixedKey = getKey(key)
            putBoolean(prefixedKey, value)
            commit()
        }
    }
}