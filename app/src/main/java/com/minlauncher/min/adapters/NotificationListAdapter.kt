package com.minlauncher.min.adapters

import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.models.AppNotificationListItem
import java.text.SimpleDateFormat
import java.util.*

class NotificationListAdapter(val notifications: List<AppNotificationListItem>, val onClickListener: NotificationListClickListener) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val labelTextView = view.findViewById<TextView>(R.id.notificationLabel)
        val descriptionTextView = view.findViewById<TextView>(R.id.notificationText)
        val iconImageView = view.findViewById<ImageView>(R.id.notificationIcon)
        val applicationLabelTextView = view.findViewById<TextView>(R.id.notificationApplicationLabel)
        val notificationTimeTextClock = view.findViewById<TextClock>(R.id.notificationTime)
        val notificationImageView = view.findViewById<ImageView>(R.id.notificationImageView)
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

        setTextView(holder.labelTextView, notification.title)
        setTextView(holder.descriptionTextView, notification.desc)

        holder.iconImageView.setImageDrawable(notification.icon)
        holder.applicationLabelTextView.text = notification.applicationLabel
        holder.notificationTimeTextClock.text = SimpleDateFormat().format(Date(notification.postDate))

        holder.view.setOnClickListener {
            onClickListener.onClick(position)
        }

        holder.notificationImageView.visibility = View.GONE
        notification.bitmap?.also {
            holder.notificationImageView.setImageDrawable(BitmapDrawable(notification.bitmap))
            holder.notificationImageView.visibility = View.VISIBLE
        }
    }

    private fun setTextView(textView: TextView, value: String) {
        if (value != "null") {
            textView.visibility = View.VISIBLE
            textView.text = value
        } else {
            textView.visibility = View.GONE
        }
    }
}