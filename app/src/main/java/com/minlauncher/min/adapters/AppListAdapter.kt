package com.minlauncher.min.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.models.AppListItem
import com.viethoa.RecyclerViewFastScroller

class AppListAdapter(val apps: List<AppListItem>) : RecyclerViewFastScroller.BubbleTextGetter, RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View, val viewType: Int) : RecyclerView.ViewHolder(view) {
        var textView: TextView? = null
        var imageView: ImageView? = null
        var separatorLabelView: TextView? = null

        init {
            if (viewType == 0) {
                textView = view.findViewById<TextView>(R.id.appLabel)
                imageView = view.findViewById<ImageView>(R.id.appIcon)
            } else {
                separatorLabelView = view.findViewById<TextView>(R.id.appSeparatorLabel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = apps[position]
        return if (item.separator) { 1 } else { 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.app_list_item, parent, false)

            return ViewHolder(view, viewType)
        } else {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.app_list_separator, parent, false)

            return ViewHolder(view, viewType)
        }
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = apps[position]
        if (holder.viewType == 0) {
            holder.textView?.text = item.label
            holder.imageView?.setImageDrawable(item.icon)

            holder.view.setOnClickListener{ v ->
                item.packageName?.let {
                    val intent = v.context.packageManager.getLaunchIntentForPackage(it)
                    v.context.startActivity(intent)
                }
            }

        } else {
            holder.separatorLabelView?.text = item.label
        }
    }

    override fun getTextToShowInBubble(pos: Int): String? {
        if (pos < 0 || pos >= apps.size)
            return null;

        val name = apps[pos].label
        if (name == null || name.length < 1)
            return null;

        return name.substring(0, 1);
    }
}