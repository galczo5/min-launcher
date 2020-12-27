package com.minlauncher.min.intents

import android.content.Intent

class HomeHideSettingChangedIntent {

    companion object {
        const val ACTION = "hide_home_changed"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}