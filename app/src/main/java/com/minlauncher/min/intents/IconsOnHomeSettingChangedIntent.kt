package com.minlauncher.min.intents

import android.content.Intent

class IconsOnHomeSettingChangedIntent {

    companion object {
        const val ACTION = "changed_icons_on_home"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}