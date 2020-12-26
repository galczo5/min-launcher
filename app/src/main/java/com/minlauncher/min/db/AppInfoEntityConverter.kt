package com.minlauncher.min.db

import com.minlauncher.min.models.AppInfo
import java.util.*

class AppInfoEntityConverter {

    companion object {
        fun toAppInfo(entity: AppInfoEntity): AppInfo {
            var date: Date? = null
            if (entity.lastUse != null) {
                date = Date(entity.lastUse)
            }

            return AppInfo(
                entity.id,
                entity.label,
                entity.packageName,
                entity.home,
                entity.hidden,
                date
            )
        }

        fun toAppInfo(entities: List<AppInfoEntity>): List<AppInfo> {
            return entities.map { AppInfoEntityConverter.toAppInfo(it) }
        }
     }

}