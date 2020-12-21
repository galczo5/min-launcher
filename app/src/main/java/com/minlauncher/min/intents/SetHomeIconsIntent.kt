package com.minlauncher.min.intents

import android.content.Intent

class SetHomeIconsIntent {

    companion object {
        const val ACTION = "set_home_icons"
        const val EXTRA = "icons_on"

        fun create(iconsOn: Boolean): Intent {
            val intent = Intent(ACTION)
            intent.putExtra(EXTRA, iconsOn)
            return intent
        }
    }

}