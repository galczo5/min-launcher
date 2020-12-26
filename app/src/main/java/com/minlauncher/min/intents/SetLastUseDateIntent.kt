package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent
import com.minlauncher.min.services.AppsService

class SetLastUseDateIntent {

    companion object {
        const val ACTION = "set_last_use"
        const val EXTRA = "id"

        fun create(context: Context, id: Int): Intent {
            val intent = Intent(context, AppsService::class.java)
            intent.putExtra("action", ACTION)
            intent.putExtra(EXTRA, id)
            return intent
        }
    }

}