package com.minlauncher.min.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.minlauncher.min.fragments.AppList
import com.minlauncher.min.fragments.Home
import com.minlauncher.min.fragments.Notifications

class MainActivityScreensAdapter(fragment: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { Notifications() }
            1 -> { Home() }
            else -> { AppList() }
        }
    }

}