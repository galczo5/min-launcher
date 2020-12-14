package com.minlauncher.min.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.adapters.NotificationListAdapter
import com.minlauncher.min.intents.RefreshNotificationListIntent
import com.minlauncher.min.models.AppNotificationListItem
import com.minlauncher.min.services.NotificationsService

class Notifications : Fragment() {

    val notificationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            view?.let { setRecyclerView(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        NotificationsService.getNotifications()

        activity?.registerReceiver(notificationsReceiver, IntentFilter(RefreshNotificationListIntent.ACTION))
//        startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));

        setRecyclerView(view)

        return view
    }

    private fun setRecyclerView(view: View) {
        val packageManager = activity?.packageManager
        val recyclerView = view.findViewById<RecyclerView>(R.id.notificationsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NotificationListAdapter(NotificationsService.getNotifications().map {
            val icon = packageManager!!.getApplicationIcon(it.packageName)
            AppNotificationListItem(icon, it.title, it.desc)
        })
    }

}