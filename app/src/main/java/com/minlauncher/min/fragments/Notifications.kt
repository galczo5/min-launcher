package com.minlauncher.min.fragments

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.minlauncher.min.R
import com.minlauncher.min.adapters.NotificationListAdapter
import com.minlauncher.min.adapters.NotificationListClickListener
import com.minlauncher.min.intents.RefreshNotificationListIntent
import com.minlauncher.min.models.AppNotification
import com.minlauncher.min.models.AppNotificationListItem
import com.minlauncher.min.services.NotificationsService

class Notifications : Fragment() {

    var notifications = listOf<AppNotification>()
    lateinit var recyclerView: RecyclerView

    val notificationsReceiver = object : BroadcastReceiver() {
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

        activity?.registerReceiver(notificationsReceiver, IntentFilter(RefreshNotificationListIntent.ACTION))

        notifications = NotificationsService.getNotifications()

        setRecyclerView()
        setItemTouchHelper()

        return view
    }

    private fun setRecyclerView() {
        val packageManager = activity?.packageManager
        val items = notifications.map {
            val icon = packageManager!!.getApplicationIcon(it.packageName)
            val applicationInfo = packageManager.getApplicationInfo(it.packageName, 0)
            val applicationLabel = packageManager.getApplicationLabel(applicationInfo).toString()
            AppNotificationListItem(icon, applicationLabel, it.title, it.desc, it.postDate)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = NotificationListAdapter(items, object : NotificationListClickListener {
            override fun onClick(position: Int) {
                notifications[position].contentIntent.send()
            }
        })
    }

    private fun setItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val notificationService: NotificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val notification = notifications[viewHolder.adapterPosition]
                notificationService.cancel(notification.tag, notification.id)

                activity?.sendBroadcast(RefreshNotificationListIntent.create())
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}