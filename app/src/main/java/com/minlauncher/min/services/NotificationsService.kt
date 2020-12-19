package com.minlauncher.min.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.minlauncher.min.intents.RefreshNotificationListIntent
import com.minlauncher.min.models.AppNotification

class NotificationsService : NotificationListenerService() {

    companion object {
        private var instance: NotificationsService? = null

        fun getNotifications(): List<AppNotification> {
            return if (instance == null) {
                listOf()
            } else {
                instance!!.activeNotifications
                    .map {
                        val title = it.notification.extras.getCharSequence("android.title").toString()
                        val text = it.notification.extras.getCharSequence("android.text").toString()
                        AppNotification(
                            it.id,
                            it.tag,
                            it.packageName,
                            it.notification.contentIntent,
                            title,
                            text,
                            it.postTime
                        )
                    }
            }
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        sendBroadcast(RefreshNotificationListIntent.create())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sendBroadcast(RefreshNotificationListIntent.create())
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sendBroadcast(RefreshNotificationListIntent.create())
    }

}