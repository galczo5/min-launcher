package com.minlauncher.min.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "app_info")
class AppInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "home") val home: Boolean,
    @ColumnInfo(name = "hidden") val hidden: Boolean,
    @ColumnInfo(name = "last_use") val lastUse: Long?) {
}