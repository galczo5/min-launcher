package com.minlauncher.min.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.Constants
import com.minlauncher.min.R
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.models.ContextMenuGroup

class HiddenAppListAdapter(val apps: List<AppInfo>, val onClickListener: HiddenAppOnClickListener) : RecyclerView.Adapter<HiddenAppListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.hiddenAppLabel)
        val showImageView = view.findViewById<ImageView>(R.id.hiddenAppShow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hidden_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = apps[position]
        holder.textView.text = item.label
        holder.showImageView.setOnClickListener {
            onClickListener.onClick(position)
        }
    }
}