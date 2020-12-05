package com.minlauncher.min.fragments

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.adapters.AppListAdapter
import com.minlauncher.min.R
import com.minlauncher.min.models.AppListItem

class AppList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)

        val packageManager = activity?.getPackageManager()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = packageManager?.queryIntentActivities(intent, 0)

        allApps?.sortBy { resolveInfo -> resolveInfo.loadLabel(packageManager).toString() }

        val items = mutableListOf<AppListItem>()
        var headingLetter: String? = null
        allApps?.forEach { resolveInfo ->
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)

            val labelFirstLetter = label[0].toUpperCase().toString();

            if (headingLetter != labelFirstLetter) {
                headingLetter = labelFirstLetter;
                items.add(AppListItem(labelFirstLetter, ShapeDrawable(), true))
            }

            val appListItem = AppListItem(label, icon, false)
            items.add(appListItem)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.appList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = items?.let { AppListAdapter(items) }

        return view
    }
}