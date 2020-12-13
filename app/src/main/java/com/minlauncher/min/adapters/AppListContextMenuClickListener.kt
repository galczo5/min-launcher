package com.minlauncher.min.adapters

interface AppListContextMenuClickListener {
    fun onHide(label: String, packageName: String)
    fun onAddToHome(label: String, packageName: String)
}