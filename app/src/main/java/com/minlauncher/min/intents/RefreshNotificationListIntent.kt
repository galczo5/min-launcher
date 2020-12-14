package com.minlauncher.min.intents

import android.content.Context
import android.content.Intent

class RefreshNotificationListIntent {

    companion object {
        const val ACTION = "refresh_notifications"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}