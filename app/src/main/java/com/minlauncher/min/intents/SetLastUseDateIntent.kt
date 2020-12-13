package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent
import com.minlauncher.min.services.AppsService

class SetLastUseDateIntent {

    companion object {
        const val ACTION = "set_last_use"

        fun create(context: Context, label: String, packageName: String): Intent {
            val intent = Intent(context, AppsService::class.java)
            intent.putExtra("action", ACTION)
            intent.putExtra("packageName", packageName)
            intent.putExtra("label", label)
            return intent
        }
    }

}