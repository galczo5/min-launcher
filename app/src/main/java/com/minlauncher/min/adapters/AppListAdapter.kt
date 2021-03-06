package com.minlauncher.min.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.models.AppListItem
import com.minlauncher.min.models.ContextMenuGroup
import com.viethoa.RecyclerViewFastScroller

class AppListAdapter(val apps: List<AppListItem>,
                     val onClickListener: AppListOnClickListener,
                     val onContextMenuClickListener: AppListContextMenuClickListener)
    : RecyclerViewFastScroller.BubbleTextGetter, RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View, val viewType: Int) : RecyclerView.ViewHolder(view) {
        lateinit var textView: TextView
        lateinit var imageView: ImageView
        lateinit var separatorLabelView: TextView

        init {
            if (viewType == 0) {
                textView = view.findViewById(R.id.appLabel)
                imageView = view.findViewById(R.id.appIcon)
            } else {
                separatorLabelView = view.findViewById(R.id.appSeparatorLabel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = apps[position]
        return if (item.separator) { 1 } else { 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val resource = if (viewType == 0) {
            R.layout.app_list_item
        } else {
            R.layout.app_list_separator
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(resource, parent, false)

        return ViewHolder(view, viewType)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = apps[position]
        if (holder.viewType == 0) {
            holder.textView.text = item.label
            holder.imageView.visibility = if (item.iconsVisible) {
                View.VISIBLE
            } else {
                View.GONE
            }

            holder.imageView.setImageDrawable(item.icon)

            holder.view.setOnClickListener{ v ->
                onClickListener.onClick(position)
            }

            holder.view.setOnCreateContextMenuListener { menu, v, menuInfo ->
                menu.add(ContextMenuGroup.ADD_TO_HOME.value, item.index, 0, "Add to home screen")
                    .setOnMenuItemClickListener {
                        onContextMenuClickListener.onAddToHome(item.id)
                        true
                    }

                menu.add(ContextMenuGroup.HIDE_FROM_LIST.value, item.index, 0, "Hide app")
                    .setOnMenuItemClickListener {
                        onContextMenuClickListener.onHide(item.id)
                        true
                    }
            }

        } else {
            holder.separatorLabelView.text = item.label
        }
    }

    override fun getTextToShowInBubble(pos: Int): String? {
        if (pos < 0 || pos >= apps.size)
            return null;

        val name = apps[pos].label
        if (name.isEmpty())
            return null;

        return name.substring(0, 1);
    }

}