package com.minlauncher.min.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.minlauncher.min.fragments.AppList
import com.minlauncher.min.fragments.Home
import com.minlauncher.min.fragments.NotificationsWrapper

class MainActivityScreensAdapter(val fragmentManager: FragmentManager,
                                 lifecycle: Lifecycle,
                                 var hideNotifications: Boolean,
                                 var hideHome: Boolean) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private var size: Int = 0
    private val list = mutableListOf<Fragment>()

    init {
        size = 5;

        if (hideNotifications) {
            size -= 1
        } else {
            list.add(NotificationsWrapper())
        }

        if (hideHome) {
            size -= 1
        } else {
            list.add(Home())
        }

        list.add(AppList())
    }

    fun clear() {
        for (i in list.toList().indices) {
            val transaction = fragmentManager.beginTransaction()
            if (i < list.size) {
                val fragment = list[i]
                transaction.remove(fragment).commit()
                list.clear()
            }
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { Fragment() }
            else -> {
                if (position - 1 < list.size) {
                    list[position - 1]
                } else {
                    Fragment()
                }
            }
        }
    }

}