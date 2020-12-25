package com.minlauncher.min.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minlauncher.min.R
import com.minlauncher.min.intents.RefreshNotificationListIntent
import com.minlauncher.min.services.NotificationsService

class NotificationsWrapper : Fragment() {

    private var notificationsCount: Int = 0
    private var paused: Boolean = true

    private val notificationBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            getNotificationCount()
            if (!paused) {
                setNotificationsFragment()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications_wrapper, container, false)
        activity?.registerReceiver(notificationBroadcastReceiver, IntentFilter(RefreshNotificationListIntent.ACTION))
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(notificationBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        paused = false
        getNotificationCount()
        setNotificationsFragment()
    }

    override fun onPause() {
        super.onPause()
        paused = true
    }

    private fun getNotificationCount() {
        notificationsCount = NotificationsService.getNotifications().size
    }

    private fun setNotificationsFragment() {
        activity?.supportFragmentManager?.also {
            val fragment = if (notificationsCount == 0) {
                EmptyNotifications()
            } else {
                Notifications()
            }

            val transaction = it.beginTransaction()
            val wrapperFragment = R.id.notificationsWrapperFragment

            transaction.replace(wrapperFragment, fragment)
            transaction.commit()
        }
    }
}