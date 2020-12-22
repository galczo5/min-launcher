package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent
import com.minlauncher.min.services.SettingsService

class IconsOnHomeSettingChangedIntent {

    companion object {
        const val EXTRA_KEY = "value";
        const val ACTION = "changed_icons_on_home"

        fun create(context: Context, value: Boolean): Intent {
            val intent = Intent(context, SettingsService::class.java)
            intent.putExtra(EXTRA_KEY, value)
            return intent
        }
    }

}