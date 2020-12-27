package com.minlauncher.min.intents

import android.content.Intent

class IconsHideSettingChangedIntent {

    companion object {
        const val ACTION = "hide_icons_changed"

        fun create(): Intent {
            return Intent(ACTION)
        }
    }

}