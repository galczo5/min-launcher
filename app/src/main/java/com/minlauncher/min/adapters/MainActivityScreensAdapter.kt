package com.minlauncher.min.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.minlauncher.min.fragments.AppList
import com.minlauncher.min.fragments.Home
import com.minlauncher.min.fragments.NotificationsWrapper

class MainActivityScreensAdapter(fragment: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        val appList = AppList()
        val notifications = NotificationsWrapper()
        val home = Home()

        return when (position) {
            1 -> { notifications }
            2 -> { home }
            3 -> { appList }
            else -> { Fragment() }
        }
    }

}