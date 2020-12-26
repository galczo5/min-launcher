package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent
import com.minlauncher.min.services.AppsService

class MarkAppAsHiddenIntent {

    companion object {
        const val EXTRA = "id";
        const val ACTION = "hide_app"

        fun create(context: Context, id: Int): Intent {
            val intent = Intent(context, AppsService::class.java)
            intent.putExtra("action", ACTION)
            intent.putExtra(EXTRA, id)
            return intent
        }
    }

}