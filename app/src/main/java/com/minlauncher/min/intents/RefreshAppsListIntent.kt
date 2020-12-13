package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent

class RefreshAppsListIntent {

    companion object {
        const val ACTION = "refresh_apps"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}