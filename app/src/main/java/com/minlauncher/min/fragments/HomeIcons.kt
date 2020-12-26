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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.minlauncher.min.R
import com.minlauncher.min.adapters.HomeAppGridAdapter
import com.minlauncher.min.adapters.HomeAppListContextMenuClickListener
import com.minlauncher.min.intents.RefreshAppsListIntent
import com.minlauncher.min.intents.UnpinAppIntent
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.models.HomeGridItem
import com.minlauncher.min.services.AppsService
import kotlinx.coroutines.launch

class HomeIcons : Fragment() {

    private var homeApps = listOf<AppInfo>()
    private var paused: Boolean = true
    lateinit var recyclerView: RecyclerView
    lateinit var packageManager: PackageManager

    private val appsRefreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            lifecycleScope.launch {
                homeApps = AppsService.homeApps()
                if (!paused) {
                    setRecyclerView()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_icons, container, false)
        activity?.packageManager?.also { packageManager = it }
        activity?.registerReceiver(appsRefreshReceiver, IntentFilter(RefreshAppsListIntent.ACTION))
        recyclerView = view.findViewById(R.id.homeAppGrid)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(appsRefreshReceiver)
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    override fun onResume() {
        paused = false
        super.onResume()

        lifecycleScope.launch {
            homeApps = AppsService.homeApps()
            setRecyclerView()
        }
    }

    private fun setRecyclerView() {
        val contextMenuClickListener = object : HomeAppListContextMenuClickListener {
            override fun onRemoveFromHome(id: Int) {
                activity?.baseContext?.also {
                    val intent = UnpinAppIntent.create(it, id)
                    activity?.startService(intent)
                }
            }
        }

        val items = getItems()
        recyclerView.layoutManager = FlexboxLayoutManager(context)
        recyclerView.adapter = HomeAppGridAdapter(items, contextMenuClickListener)
    }

    private fun getItems(): List<HomeGridItem> {
        return homeApps.map {
            val icon = packageManager.getApplicationIcon(it.packageName)
            HomeGridItem(it.id, it.label, it.packageName, icon)
        }
    }
}