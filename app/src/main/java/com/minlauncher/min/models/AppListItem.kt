package com.minlauncher.min.models

import android.graphics.drawable.Drawable


class AppListItem(
        val label: String,
        val packageName: String?,
        val icon: Drawable,
        val separator: Boolean) {
}