package com.minlauncher.min.models

import java.util.*

class AppInfo(
    val label: String,
    val packageName: String,
    val home: Boolean,
    val hidden: Boolean,
    val lastUse: Date?) {
}