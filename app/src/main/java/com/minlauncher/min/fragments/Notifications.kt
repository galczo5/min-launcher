package com.minlauncher.min.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.adapters.NotificationListAdapter
import com.minlauncher.min.adapters.NotificationListClickListener
import com.minlauncher.min.intents.RefreshNotificationListIntent
import com.minlauncher.min.models.AppNotification
import com.minlauncher.min.models.AppNotificationListItem
import com.minlauncher.min.services.NotificationsService

class Notifications : Fragment() {

    lateinit var recyclerView: RecyclerView

    private var paused: Boolean = true
    private var notifications = listOf<AppNotification>()
    private val notificationsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            notifications = NotificationsService.getNotifications()
            setRecyclerView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        recyclerView = view.findViewById(R.id.notificationsRecyclerView)
        notifications = NotificationsService.getNotifications()

        activity?.registerReceiver(notificationsReceiver, IntentFilter(RefreshNotificationListIntent.ACTION))
        setRecyclerView()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(notificationsReceiver)
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    override fun onResume() {
        paused = false
        super.onResume()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        var items = listOf<AppNotificationListItem>()

        val packageManager = activity?.packageManager
        packageManager?.also {
            items = getItems(it)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NotificationListAdapter(items, object : NotificationListClickListener {
            override fun onClick(position: Int) {
                notifications[position].contentIntent.send()
            }
        })
    }

    private fun getItems(packageManager: PackageManager): List<AppNotificationListItem> {
        return notifications.map {
            val icon = packageManager.getApplicationIcon(it.packageName)
            val applicationInfo = packageManager.getApplicationInfo(it.packageName, 0)
            val applicationLabel = packageManager.getApplicationLabel(applicationInfo).toString()
            AppNotificationListItem(icon, applicationLabel, it.title, it.desc, it.postDate)
        }
    }

}