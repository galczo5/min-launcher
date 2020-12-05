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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minlauncher.min.Constants
import com.minlauncher.min.R
import com.minlauncher.min.adapters.HomeAppListAdapter
import com.minlauncher.min.models.HomeListItem


class Home : Fragment() {

    var homeApps = mutableListOf<HomeListItem>()
    var batteryStatusTextView: TextView? = null

    val batteryStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scale = intent!!.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (batteryStatusTextView != null) {
                batteryStatusTextView?.text = "$scale% battery"
            }
        }
    }

    val homeListChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadHomeAppsFromSharedPreferences()
            view?.let { setRecyclerView(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setBatteryStatus(view)
        loadHomeAppsFromSharedPreferences()
        setRecyclerView(view)

        val intentFilter = IntentFilter(Constants.REFRESH_HOME_INTENT.value)
        activity?.registerReceiver(homeListChangedReceiver, intentFilter)

        return view
    }

    private fun setBatteryStatus(view: View) {
        batteryStatusTextView = view.findViewById<TextView>(R.id.batteryStatus)
        activity?.registerReceiver(
            batteryStatusReceiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
    }

    private fun loadHomeAppsFromSharedPreferences() {
        val preferencesName = Constants.SHARED_PREFERENCES_NAME.value
        val preferencesKey = Constants.SHARED_PREFERENCES_HOME_APPS_KEY.value

        val appsSerialized = activity?.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            ?.getString(preferencesKey, "[]")

        val gson = Gson()
        val type = TypeToken.getParameterized(List::class.java, HomeListItem::class.java).type
        homeApps = gson.fromJson<ArrayList<HomeListItem>>(appsSerialized, type)
    }

    private fun setRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.homeAppList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = HomeAppListAdapter(homeApps)
    }

}