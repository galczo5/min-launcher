package com.minlauncher.min.fragments

import android.content.Context
import android.content.Intent
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
import com.minlauncher.min.models.*
import com.viethoa.RecyclerViewFastScroller
import com.viethoa.models.AlphabetItem

class AppList : Fragment() {

    var appInfoSharedPreferences: AppInfoSharedPreferences? = null
    var items = mutableListOf<AppListItem>()
    var alphabet = mutableListOf<AlphabetItem>()

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

        setViews(view)

        val settingsCog = view.findViewById<ImageView>(R.id.settingsCog)
        settingsCog.setOnClickListener {
            Intent(activity, SettingsActivity::class.java).also {
                startActivity(it)
            }
        }

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.appListSwipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            setViews(view)
            swipeRefreshLayout.isRefreshing = false;
        }

        return view
    }

    private fun setViews(view: View) {
        val allApps = getInstalledApps()
        allApps?.let { appInfoSharedPreferences?.refreshApps(it) }

        setData()
        setAlphabet()

        setRecyclerView(view)
        setDarkModeSwitch(view)
    }

    private fun setDarkModeSwitch(view: View) {
        val switch = view.findViewById<Switch>(R.id.darkModeSwitch)

        val sharedPreferences = activity?.getSharedPreferences(
            Constants.DARK_MODE_SHARED_PREFERENCES_NAME.value,
            Context.MODE_PRIVATE
        )

        val darkModeOn = sharedPreferences?.getInt(
            Constants.DARK_MODE_SHARED_PREFERENCES_KEY.value,
            AppCompatDelegate.MODE_NIGHT_NO
        )

        switch.isChecked = darkModeOn == AppCompatDelegate.MODE_NIGHT_YES
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            val intentName = if (isChecked) {
                Constants.DARK_MODE_ON.value
            } else {
                Constants.DARK_MODE_OFF.value
            }
            val intent = Intent(intentName)
            activity?.sendBroadcast(intent)

        }
    }

    private fun getInstalledApps(): List<AppInfo>? {
        val packageManager = activity?.getPackageManager()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = packageManager?.queryIntentActivities(intent, 0)

        allApps?.sortBy { resolveInfo ->
            resolveInfo.loadLabel(packageManager).toString().toUpperCase()
        }

        return allApps?.map {
            val label = it.loadLabel(packageManager).toString()
            val packageName = it.activityInfo.packageName

            AppInfo(label, packageName, false, false)
        }
    }

    private fun setRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.appList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AppListAdapter(items)

        val fastScroller = view.findViewById<RecyclerViewFastScroller>(R.id.fastScroller)
        fastScroller.setUpAlphabet(alphabet)
        fastScroller.setRecyclerView(recyclerView)
    }

    fun setData() {
        val packageManager = activity?.packageManager
        var headingLetter: String? = null

        items = mutableListOf()

        val apps = appInfoSharedPreferences?.getApps()
        apps?.forEachIndexed{ index, appInfo ->
            val packageName = appInfo.packageName
            val label = appInfo.label

            val icon = packageManager?.getApplicationIcon(packageName)
            val labelFirstLetter = label[0].toUpperCase().toString()

            if (headingLetter != labelFirstLetter) {
                val separatorLabel = if (labelFirstLetter.isDigitsOnly()) "0-9" else labelFirstLetter
                val appListItem = AppListItem(separatorLabel, null, ShapeDrawable(), true, index)
                items.add(appListItem)
            }

            headingLetter = labelFirstLetter

            icon?.let {
                val appListItem = AppListItem(label, packageName, it, false, index)
                items.add(appListItem)
            }
        }
    }

    private fun setAlphabet() {
        alphabet = mutableListOf()
        items.forEachIndexed { index, item ->
            if (item.separator) {
                val firstLetter = item.label[0].toUpperCase().toString()
                alphabet.add(AlphabetItem(index, firstLetter, false))
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.groupId == ContextMenuGroup.ADD_TO_HOME.value) {
            appInfoSharedPreferences?.getApps()?.let {
                val appInfo = it[item.itemId]
                val updatedAppInfo = AppInfo(appInfo.label, appInfo.packageName, true, appInfo.hidden)
                appInfoSharedPreferences?.update(updatedAppInfo)
            }
        } else if (item.groupId == ContextMenuGroup.REMOVE_FROM_HOME.value) {
            appInfoSharedPreferences?.getHomeApps()?.let {
                val appInfo = it[item.itemId]
                val updatedAppInfo = AppInfo(appInfo.label, appInfo.packageName, false, appInfo.hidden)
                appInfoSharedPreferences?.update(updatedAppInfo)
            }
        } else if (item.groupId == ContextMenuGroup.HIDE_FROM_LIST.value) {
            appInfoSharedPreferences?.getApps()?.let {
                val appInfo = it[item.itemId]
                val updatedAppInfo = AppInfo(appInfo.label, appInfo.packageName, appInfo.home, true)
                appInfoSharedPreferences?.update(updatedAppInfo)
            }
        }

        activity?.sendBroadcast(Intent(Constants.REFRESH_HOME_INTENT.value))

        return true
    }
}