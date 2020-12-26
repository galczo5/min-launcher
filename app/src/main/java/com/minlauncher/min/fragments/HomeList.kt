package com.minlauncher.min.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.adapters.HomeAppListAdapter
import com.minlauncher.min.adapters.HomeAppListContextMenuClickListener
import com.minlauncher.min.intents.RefreshAppsListIntent
import com.minlauncher.min.intents.UnpinAppIntent
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.services.AppsService
import kotlinx.coroutines.launch

class HomeList : Fragment() {

    private var paused: Boolean = true
    private var homeApps = listOf<AppInfo>()
    private lateinit var recyclerView: RecyclerView

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
        val view = inflater.inflate(R.layout.fragment_home_list, container, false)
        recyclerView = view.findViewById(R.id.homeAppList)
        activity?.registerReceiver(appsRefreshReceiver, IntentFilter(RefreshAppsListIntent.ACTION))
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

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HomeAppListAdapter(homeApps, contextMenuClickListener)
    }
}