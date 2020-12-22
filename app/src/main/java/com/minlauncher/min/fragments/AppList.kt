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
import com.minlauncher.min.services.SettingsService
import com.viethoa.RecyclerViewFastScroller
import com.viethoa.models.AlphabetItem

class AppList : Fragment() {

    var apps = listOf<AppInfo>()
    var lastUsedApps = listOf<AppInfo>()
    var hideIcons: Boolean = false;

    lateinit var settingsCog: ImageView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var switch: Switch
    lateinit var recyclerView: RecyclerView
    lateinit var fastScroller: RecyclerViewFastScroller
    lateinit var packageManager: PackageManager
    private var paused: Boolean = true

    private val appsRefreshReceivers = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            apps = AppsService.allApps()
            lastUsedApps = AppsService.lastUsed()
            if (!paused) {
                setRecyclerView()
            }
        }
    }

    private val hideIconsSettingBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            hideIcons = SettingsService.hideIcons()
            if (!paused) {
                setRecyclerView()
            }
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

        activity?.packageManager?.let {
            packageManager = it
        }

        activity?.registerReceiver(appsRefreshReceivers, IntentFilter(RefreshAppsListIntent.ACTION))
        activity?.registerReceiver(hideIconsSettingBroadcastReceiver, IntentFilter(IconsHideSettingChangedIntent.ACTION))

        apps = AppsService.allApps()
        if (apps.isEmpty()) {
            reloadList()
        }

        setSettingsCog()
        setSwipeRefresh()
        setDarkModeSwitch()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(appsRefreshReceivers)
        activity?.unregisterReceiver(hideIconsSettingBroadcastReceiver)
    }

    override fun onPause() {
        paused = true
        super.onPause()
    }

    override fun onResume() {
        paused = false
        super.onResume()

        hideIcons = SettingsService.hideIcons()
        setRecyclerView()
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
            Constants.DARK_MODE_SHARED_PREFERENCES_NAME.VALUE,
            Context.MODE_PRIVATE
        )

        val darkModeOn = sharedPreferences?.getInt(
            Constants.DARK_MODE_SHARED_PREFERENCES_KEY.VALUE,
            AppCompatDelegate.MODE_NIGHT_NO
        )

        switch.isChecked = darkModeOn == AppCompatDelegate.MODE_NIGHT_YES
        switch.setOnCheckedChangeListener { _, isChecked ->
            val intentName = if (isChecked) Constants.DARK_MODE_ON.VALUE else Constants.DARK_MODE_OFF.VALUE
            val intent = Intent(intentName)
            activity?.sendBroadcast(intent)
        }
    }

    private fun setRecyclerView() {
        val items = getItems()
        val baseContext = activity?.baseContext
        val clickListener = object : AppListOnClickListener {
            override fun onClick(position: Int) {
                val item = items[position]
                baseContext?.also {
                    val intent = SetLastUseDateIntent.create(it, item.label, item.packageName)
                    activity?.startService(intent)
                }

                val intent = packageManager.getLaunchIntentForPackage(item.packageName)
                context?.startActivity(intent)
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

        val alphabet = getAlphabet(items)
        fastScroller.setUpAlphabet(alphabet)
        fastScroller.setRecyclerView(recyclerView)
    }

    private fun getItems(): List<AppListItem> {
        var headingLetter: String? = null
        val items = mutableListOf<AppListItem>()
        lastUsedApps.forEachIndexed { index, appInfo ->
            items.add(getAppListItem(appInfo, index))
        }

        apps.forEachIndexed { index, appInfo ->
            val labelFirstLetter = appInfo.label[0].toUpperCase().toString()
            if (headingLetter != labelFirstLetter) {
                items.add(getSeparator(labelFirstLetter, index))
            }

            headingLetter = labelFirstLetter
            items.add(getAppListItem(appInfo, lastUsedApps.size + index))
        }

        return items.toList()
    }

    private fun getAppListItem(appInfo: AppInfo, index: Int): AppListItem {
        val packageName = appInfo.packageName
        val label = appInfo.label

        val icon = packageManager.getApplicationIcon(packageName)
        return AppListItem(label, packageName, icon, false, index, !hideIcons)
    }

    private fun getSeparator(labelFirstLetter: String, index: Int): AppListItem {
        val separatorLabel = if (labelFirstLetter.isDigitsOnly()) "0-9" else labelFirstLetter
        return AppListItem(separatorLabel, "", ShapeDrawable(), true, index, false)
    }

    private fun getAlphabet(items: List<AppListItem>): List<AlphabetItem> {
        return items.mapIndexed { index, item ->
            if (item.separator && item.label != "0-9") {
                val firstLetter = item.label[0].toUpperCase().toString()
                AlphabetItem(index, firstLetter, false)
            } else {
                null
            }
        }.filterNotNull()
    }
}