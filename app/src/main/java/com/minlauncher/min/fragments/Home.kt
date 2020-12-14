package com.minlauncher.min.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.R
import com.minlauncher.min.adapters.HomeAppListAdapter
import com.minlauncher.min.adapters.HomeAppListContextMenuClickListener
import com.minlauncher.min.intents.RefreshAppsListIntent
import com.minlauncher.min.intents.UnpinAppIntent
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.services.AppsService

class Home : Fragment() {

    var homeApps = listOf<AppInfo>()
    lateinit var batteryStatusTextView: TextView
    lateinit var recyclerView: RecyclerView

    private val batteryStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scale = intent!!.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            batteryStatusTextView.text = "$scale% battery"
        }
    }

    private val appsRefreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            homeApps = AppsService.homeApps()
            setRecyclerView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        batteryStatusTextView = view.findViewById(R.id.batteryStatus)
        recyclerView = view.findViewById(R.id.homeAppList)

        homeApps = AppsService.homeApps()
        setRecyclerView()

        activity?.registerReceiver(batteryStatusReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        activity?.registerReceiver(appsRefreshReceiver, IntentFilter(RefreshAppsListIntent.ACTION))

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(appsRefreshReceiver)
        activity?.unregisterReceiver(batteryStatusReceiver)
    }

    private fun setRecyclerView() {
        val contextMenuClickListener = object : HomeAppListContextMenuClickListener {
            override fun onRemoveFromHome(label: String, packageName: String) {
                activity?.baseContext?.also {
                    val intent = UnpinAppIntent.create(it, label, packageName)
                    activity?.startService(intent)
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HomeAppListAdapter(homeApps, contextMenuClickListener)
    }

}