package com.minlauncher.min.db

import androidx.room.*

@Dao
abstract class AppInfoDao {

    @Transaction
    open suspend fun updateDatabase(appInfo: List<AppInfoEntity>) {
        deleteAll()
        insertAll(appInfo)
    }

    @Query("SELECT * FROM app_info")
    abstract suspend fun all(): List<AppInfoEntity>

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
