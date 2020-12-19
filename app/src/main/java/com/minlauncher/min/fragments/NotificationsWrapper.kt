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

    private val notificationBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            setNotificationsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications_wrapper, container, false)
        setNotificationsFragment()
        activity?.registerReceiver(notificationBroadcastReceiver, IntentFilter(RefreshNotificationListIntent.ACTION))
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(notificationBroadcastReceiver)
    }

    private fun setNotificationsFragment() {
        activity?.supportFragmentManager?.also {
            val transaction = it.beginTransaction()
            val size = NotificationsService.getNotifications().size

            if (size == 0) {
                transaction.replace(R.id.notificationsWrapperFragment, EmptyNotifications())
            } else {
                transaction.replace(R.id.notificationsWrapperFragment, Notifications())
            }

            transaction.commit()
        }
    }
}