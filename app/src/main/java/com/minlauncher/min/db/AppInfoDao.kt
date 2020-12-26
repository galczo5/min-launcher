package com.minlauncher.min.db

import androidx.room.*
import java.util.*

@Dao
abstract class AppInfoDao {

    open suspend fun updateDatabase(appInfo: List<AppInfoEntity>) {
        deleteAll()
        insertAll(appInfo)
    }

    @Query("SELECT * FROM app_info")
    abstract suspend fun all(): List<AppInfoEntity>

    @Query("SELECT * FROM app_info WHERE home = 1")
    abstract suspend fun home(): List<AppInfoEntity>

    @Query("SELECT * FROM app_info WHERE hidden = 1")
    abstract suspend fun hidden(): List<AppInfoEntity>

    @Query("SELECT * FROM app_info ORDER BY last_use DESC LIMIT 5")
    abstract suspend fun lastUsed(): List<AppInfoEntity>

    @Insert
    abstract suspend fun insertAll(appInfo: List<AppInfoEntity>)

    @Query("DELETE FROM app_info")
    abstract suspend fun deleteAll()

    @Query("UPDATE app_info SET home = :home WHERE id = :id")
    abstract suspend fun setHome(id: Int, home: Boolean)

    @Query("UPDATE app_info SET hidden = :hidden WHERE id = :id")
    abstract suspend fun setHidden(id: Int, hidden: Boolean)

    @Query("UPDATE app_info SET last_use = :date WHERE id = :id")
    abstract suspend fun setLastUse(id: Int, date: Long)

}
