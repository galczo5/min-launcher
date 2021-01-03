package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent
import com.minlauncher.min.services.AppsService

class InitAppsListIntent {

    companion object {
        const val ACTION = "init_apps"

        fun create(context: Context): Intent {
            val intent = Intent(context, AppsService::class.java)
            intent.putExtra("action", ACTION)
            return intent
        }
    }

}