package com.minlauncher.min.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.models.AppNotification
import com.minlauncher.min.models.AppNotificationListItem

class NotificationListAdapter(val notifications: List<AppNotificationListItem>) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView = view.findViewById<TextView>(R.id.notificationLabel)
        val descriptionTextView = view.findViewById<TextView>(R.id.notificationText)
        val iconImageView = view.findViewById<ImageView>(R.id.notificationIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.labelTextView.text = notification.title
        holder.descriptionTextView.text = notification.desc
        holder.iconImageView.setImageDrawable(notification.icon)
    }
}