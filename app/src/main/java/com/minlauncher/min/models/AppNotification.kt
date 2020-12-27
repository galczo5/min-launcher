package com.minlauncher.min.models

import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

class AppNotification(
    val id: Int,
    val tag: String?,
    val bitmap: Bitmap?,
    val packageName: String,
    val contentIntent: PendingIntent,
    val title: String,
    val desc: String,
    val postDate: Long
) {
}