package com.minlauncher.min

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        val items = allApps?.map { resolveInfo -> resolveInfo.loadLabel(packageManager).toString() }

        val recyclerView = view.findViewById<RecyclerView>(R.id.appList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = items?.let { AppListAdapter(it) }

        return view
    }
}