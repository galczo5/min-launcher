package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent
import com.minlauncher.min.services.AppsService

class UnpinAppIntent {

    companion object {
        const val ACTION = "remove_from_home"
        const val EXTRA = "id"

        fun create(context: Context, id: Int): Intent {
            val intent = Intent(context, AppsService::class.java)
            intent.putExtra("action", ACTION)
            intent.putExtra(EXTRA, id)
            return intent
        }
    }

}