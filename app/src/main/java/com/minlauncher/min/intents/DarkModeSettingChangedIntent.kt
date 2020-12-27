package com.minlauncher.min.intents

import android.content.Intent

class DarkModeSettingChangedIntent {

    companion object {
        const val ACTION = "changed_dark_mode"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}