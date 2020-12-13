package com.minlauncher.min.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.minlauncher.min.Constants
import com.minlauncher.min.adapters.AppListAdapter
import com.minlauncher.min.R
import com.minlauncher.min.SettingsActivity
import com.minlauncher.min.adapters.AppListContextMenuClickListener
import com.minlauncher.min.adapters.AppListOnClickListener
import com.minlauncher.min.intents.*
import com.minlauncher.min.models.*
import com.minlauncher.min.services.AppsService
import com.viethoa.RecyclerViewFastScroller
import com.viethoa.models.AlphabetItem

class AppList : Fragment() {

    var items = mutableListOf<AppListItem>()
    var alphabet = mutableListOf<AlphabetItem>()

    lateinit var settingsCog: ImageView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var switch: Switch
    lateinit var recyclerView: RecyclerView
    lateinit var fastScroller: RecyclerViewFastScroller
    var packageManager: PackageManager? = null

    private val appsRefreshReceivers = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            items = mutableListOf()
            alphabet = mutableListOf()

            val apps = AppsService.allApps()
            val lastUsedApps = AppsService.lastUsed()

            setSortedApps(apps, lastUsedApps)
            setAlphabet()
            setRecyclerView()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)

        settingsCog = view.findViewById(R.id.settingsCog)
        swipeRefreshLayout = view.findViewById(R.id.appListSwipeRefresh)
        switch = view.findViewById(R.id.darkModeSwitch)
        recyclerView = view.findViewById(R.id.appList)
        fastScroller = view.findViewById(R.id.fastScroller)
        packageManager = activity?.packageManager

        activity?.registerReceiver(appsRefreshReceivers, IntentFilter(RefreshAppsListIntent.ACTION))

        reloadList()

        setSettingsCog()
        setSwipeRefresh()
        setDarkModeSwitch()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(appsRefreshReceivers)
    }

    private fun setSettingsCog() {
        settingsCog.setOnClickListener {
            Intent(activity, SettingsActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun setSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            reloadList()
            swipeRefreshLayout.isRefreshing = false;
        }
    }

    private fun reloadList() {
        val intent = activity?.baseContext?.let { ReloadAppsListIntent.create(it) }
        activity?.startService(intent)
    }

    private fun setDarkModeSwitch() {
        val sharedPreferences = activity?.getSharedPreferences(
            Constants.DARK_MODE_SHARED_PREFERENCES_NAME.value,
            Context.MODE_PRIVATE
        )

        val darkModeOn = sharedPreferences?.getInt(
            Constants.DARK_MODE_SHARED_PREFERENCES_KEY.value,
            AppCompatDelegate.MODE_NIGHT_NO
        )

        switch.isChecked = darkModeOn == AppCompatDelegate.MODE_NIGHT_YES
        switch.setOnCheckedChangeListener { _, isChecked ->
            val intentName = if (isChecked) Constants.DARK_MODE_ON.value else Constants.DARK_MODE_OFF.value
            val intent = Intent(intentName)
            activity?.sendBroadcast(intent)
        }
    }

    private fun setRecyclerView() {
        val baseContext = activity?.baseContext
        val clickListener = object : AppListOnClickListener {
            override fun onClick(position: Int) {
                val item = items[position]
                item.packageName?.let {
                    baseContext?.also {
                        val intent = SetLastUseDateIntent.create(it, item.label, item.packageName)
                        activity?.startService(intent)
                    }

                    val intent = packageManager?.getLaunchIntentForPackage(item.packageName)
                    context?.startActivity(intent)
                }
            }
        }

        val menuClickListener = object : AppListContextMenuClickListener {
            override fun onHide(label: String, packageName: String) {
                baseContext?.let { MarkAppAsHiddenIntent.create(it, label, packageName) }.also {
                    activity?.startService(it)
                }
            }

            override fun onAddToHome(label: String, packageName: String) {
                baseContext?.let { PinAppIntent.create(it, label, packageName) }.also {
                    activity?.startService(it)
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AppListAdapter(items, clickListener, menuClickListener)

        fastScroller.setUpAlphabet(alphabet)
        fastScroller.setRecyclerView(recyclerView)
    }

    private fun setSortedApps(apps: List<AppInfo>?, lastUsedApps: List<AppInfo>?) {
        var headingLetter: String? = null
        lastUsedApps?.forEachIndexed { index, appInfo ->
            val packageName = appInfo.packageName
            val label = appInfo.label

            val icon = packageManager?.getApplicationIcon(packageName)
            icon?.let {
                val appListItem = AppListItem(label, packageName, it, false, index)
                items.add(appListItem)
            }
        }

        apps?.forEachIndexed { index, appInfo ->
            val packageName = appInfo.packageName
            val label = appInfo.label

            val labelFirstLetter = label[0].toUpperCase().toString()
            if (headingLetter != labelFirstLetter) {
                addSeparator(labelFirstLetter, index)
            }

            headingLetter = labelFirstLetter

            val icon = packageManager?.getApplicationIcon(packageName)
            icon?.let {
                val appIndex = lastUsedApps?.size!! + index
                val appListItem = AppListItem(label, packageName, it, false, appIndex)
                items.add(appListItem)
            }
        }
    }

    private fun addSeparator(labelFirstLetter: String, index: Int) {
        val separatorLabel = if (labelFirstLetter.isDigitsOnly()) "0-9" else labelFirstLetter
        val appListItem = AppListItem(separatorLabel, null, ShapeDrawable(), true, index)
        items.add(appListItem)
    }

    private fun setAlphabet() {
        items.mapIndexed { index, item ->
            if (item.separator && item.label != "0-9") {
                val firstLetter = item.label[0].toUpperCase().toString()
                alphabet.add(AlphabetItem(index, firstLetter, false))
            }
        }
    }
}