package com.minlauncher.min.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
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
import com.minlauncher.min.adapters.AppListOnClickListener
import com.minlauncher.min.models.*
import com.viethoa.RecyclerViewFastScroller
import com.viethoa.models.AlphabetItem
import java.util.*

class AppList : Fragment() {

    var appInfoSharedPreferences: AppInfoSharedPreferences? = null
    var items = mutableListOf<AppListItem>()
    var alphabet = mutableListOf<AlphabetItem>()

    lateinit var settingsCog: ImageView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var switch: Switch
    lateinit var recyclerView: RecyclerView
    lateinit var fastScroller: RecyclerViewFastScroller
    var packageManager: PackageManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)
        val sharedPreferencesKey = Constants.SHARED_PREFERENCES_APPS.value
        activity?.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)?.let {
            appInfoSharedPreferences = AppInfoSharedPreferences(it)
        }

        settingsCog = view.findViewById<ImageView>(R.id.settingsCog)
        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.appListSwipeRefresh)
        switch = view.findViewById<Switch>(R.id.darkModeSwitch)
        recyclerView = view.findViewById<RecyclerView>(R.id.appList)
        fastScroller = view.findViewById<RecyclerViewFastScroller>(R.id.fastScroller)
        packageManager = activity?.getPackageManager()

        reloadList()
        setSettingsCog()
        setSwipeRefresh()
        setDarkModeSwitch()

        return view
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
        val allApps = getInstalledApps()
        allApps?.let { appInfoSharedPreferences?.refreshApps(it) }

        setData()
        setAlphabet()

        setRecyclerView()
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

    private fun getInstalledApps(): List<AppInfo>? {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = packageManager?.queryIntentActivities(intent, 0)
        allApps?.sortBy { resolveInfo ->
            resolveInfo.loadLabel(packageManager).toString().toUpperCase()
        }

        return allApps?.map {
            val label = it.loadLabel(packageManager).toString()
            val packageName = it.activityInfo.packageName
            AppInfo(label, packageName, false, false, null)
        }
    }

    private fun setRecyclerView() {
        val onClickListener = object : AppListOnClickListener {
            override fun onClick(position: Int) {
                val item = items[position]

                setLastUse(item)
                setData()

                item.packageName?.let {
                    val intent = packageManager?.getLaunchIntentForPackage(item.packageName)
                    context?.startActivity(intent)
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AppListAdapter(items, onClickListener)

        fastScroller.setUpAlphabet(alphabet)
        fastScroller.setRecyclerView(recyclerView)
    }

    private fun setLastUse(item: AppListItem) {
        appInfoSharedPreferences?.getApp(item.label, item.packageName)?.let { it ->
            val date = Date(System.currentTimeMillis())
            val updatedAppInfo = AppInfo(it.label, it.packageName, it.home, it.hidden, date)
            appInfoSharedPreferences?.update(updatedAppInfo)
        }
    }

    fun setData() {
        items = mutableListOf()

        val apps = appInfoSharedPreferences?.getApps()
        val lastUsedApps = appInfoSharedPreferences?.getLastUsed()
        setLastUsedApps(lastUsedApps)
        setSortedApps(apps, lastUsedApps)
    }

    private fun setSortedApps(apps: List<AppInfo>?, lastUsedApps: List<AppInfo>?) {
        var headingLetter: String? = null
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

    private fun setLastUsedApps(lastUsedApps: List<AppInfo>?) {
        lastUsedApps?.forEachIndexed { index, appInfo ->
            val packageName = appInfo.packageName
            val label = appInfo.label
            val icon = packageManager?.getApplicationIcon(packageName)

            icon?.let {
                val appListItem = AppListItem(label, packageName, it, false, index)
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
        alphabet = mutableListOf()
        items.mapIndexed { index, item ->
            if (item.separator && item.label != "0-9") {
                val firstLetter = item.label[0].toUpperCase().toString()
                alphabet.add(AlphabetItem(index, firstLetter, false))
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            ContextMenuGroup.ADD_TO_HOME.value -> {
                appInfoSharedPreferences?.getApps()?.let {
                    updateAppByPosition(it, item.itemId, true, null)
                }
            }
            ContextMenuGroup.REMOVE_FROM_HOME.value -> {
                appInfoSharedPreferences?.getHomeApps()?.let {
                    updateAppByPosition(it, item.itemId, false, null)
                }
            }
            ContextMenuGroup.HIDE_FROM_LIST.value -> {
                appInfoSharedPreferences?.getApps()?.let {
                    updateAppByPosition(it, item.itemId, null, true)
                }
            }
        }

        activity?.sendBroadcast(Intent(Constants.REFRESH_HOME_INTENT.value))
        return true
    }

    fun updateAppByPosition(list: List<AppInfo>, position: Int, home: Boolean?, hidden: Boolean?) {
        val appInfo = list[position]
        val updatedHome = home ?: appInfo.home
        val updatedHidden = hidden ?: appInfo.hidden
        val updatedAppInfo = AppInfo(appInfo.label, appInfo.packageName, updatedHome, updatedHidden, null)
        appInfoSharedPreferences?.update(updatedAppInfo)
    }
}