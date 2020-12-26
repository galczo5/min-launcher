package com.minlauncher.min.models

import android.graphics.drawable.Drawable


class AppListItem(
        val id: Int,
        val label: String,
        val packageName: String,
        val icon: Drawable,
        val separator: Boolean,
        val index: Int,
        val iconsVisible: Boolean)
