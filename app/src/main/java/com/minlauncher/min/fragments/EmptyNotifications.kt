package com.minlauncher.min.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.minlauncher.min.R
import com.minlauncher.min.intents.RefreshNotificationListIntent

class EmptyNotifications : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_empty_notifications, container, false)

        view.setOnLongClickListener {
            activity?.sendBroadcast(RefreshNotificationListIntent.create())
            true
        }

        return view
    }

}