package com.minlauncher.min.intents

import android.content.Intent

class NotificationsHideChangedIntent {

    companion object {
        const val ACTION = "hide_notifications_changed"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}