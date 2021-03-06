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
import com.minlauncher.min.R
import com.minlauncher.min.intents.IconsOnHomeSettingChangedIntent
import com.minlauncher.min.intents.RefreshNotificationListIntent
import com.minlauncher.min.services.NotificationsService
import com.minlauncher.min.services.SettingsService

class Home : Fragment() {

    private var paused: Boolean = true
    private var showOnlyIcons: Boolean = false
    private var batteryStatusText: String = ""
    private var numberOfNotifications: Int = 0

    lateinit var batteryStatusTextView: TextView
    lateinit var notificationsCounterView: TextView

    private val batteryStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.also {
                val scale = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                batteryStatusText = "$scale% battery"

                if (!paused) {
                    setBatteryStatusTextView()
                }
            }
        }
    }

    private val notificationsBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            numberOfNotifications = NotificationsService.getNotifications().size
            if (!paused) {
                setNotificationsCounterTextView()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        batteryStatusTextView = view.findViewById(R.id.batteryStatus)
        batteryStatusTextView.visibility = View.GONE

        notificationsCounterView = view.findViewById(R.id.homeNotificationsCounter)

        activity?.registerReceiver(batteryStatusReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        activity?.registerReceiver(notificationsBroadcastReceiver, IntentFilter(RefreshNotificationListIntent.ACTION))

        showOnlyIcons = SettingsService.homeIcons()

        setAppsFragment()
        setBatteryStatusTextView()
        setNotificationsCounterTextView()

        return view
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    override fun onResume() {
        paused = false
        super.onResume()

        if (showOnlyIcons != SettingsService.homeIcons()) {
            showOnlyIcons = SettingsService.homeIcons()
            setAppsFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(batteryStatusReceiver)
        activity?.unregisterReceiver(notificationsBroadcastReceiver)
    }

    private fun setAppsFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.also {
            val fragment = if (showOnlyIcons) {
                HomeIcons()
            } else {
                HomeList()
            }

            it.replace(R.id.homeAppsFragment, fragment)
            it.commit()
        }
    }

    private fun setNotificationsCounterTextView() {
        notificationsCounterView.text = "$numberOfNotifications active notifications"
        if (numberOfNotifications == 0) {
            notificationsCounterView.visibility = View.GONE
        } else {
            notificationsCounterView.visibility = View.VISIBLE
        }
    }

    private fun setBatteryStatusTextView() {
        batteryStatusTextView.text = batteryStatusText
        if (batteryStatusText.any()) {
            batteryStatusTextView.visibility = View.VISIBLE
        }
    }
}