package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent

class LastUsedAppsHideSettingChangedIntent {

    companion object {
        const val ACTION = "hide_last_used_apps_changed"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}