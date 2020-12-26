package com.minlauncher.min.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(AppInfoEntity::class), version = 1)
abstract class AppInfoDatabase : RoomDatabase() {

    abstract fun dao(): AppInfoDao

}