package com.minlauncher.min.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.models.ContextMenuGroup

class HomeAppListAdapter(val apps: List<AppInfo>,
                         val contextMenuClickListener: HomeAppListContextMenuClickListener)
    : RecyclerView.Adapter<HomeAppListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.homeAppLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = apps[position]

        holder.textView.text = item.label
        holder.view.setOnClickListener { v ->
            val intent = v.context.packageManager.getLaunchIntentForPackage(item.packageName)
            v.context.startActivity(intent)
        }

        holder.view.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(ContextMenuGroup.REMOVE_FROM_HOME.value, position, 0, "Remove from home screen").setOnMenuItemClickListener {
                contextMenuClickListener.onRemoveFromHome(item.label, item.packageName)
                true
            }
        }
    }
}