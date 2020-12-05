package com.minlauncher.min.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minlauncher.min.Constants
import com.minlauncher.min.adapters.AppListAdapter
import com.minlauncher.min.R
import com.minlauncher.min.models.AppListItem
import com.minlauncher.min.models.ContextMenuGroup
import com.minlauncher.min.models.HomeListItem
import com.viethoa.RecyclerViewFastScroller
import com.viethoa.models.AlphabetItem

class AppList : Fragment() {

    var items = mutableListOf<AppListItem>()
    var alphabet = mutableListOf<AlphabetItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)
        val allApps = getInstalledApps()

        allApps?.let { setData(it) }
        setRecyclerView(view)

        return view
    }

    private fun getInstalledApps(): MutableList<ResolveInfo>? {
        val packageManager = activity?.getPackageManager()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val allApps = packageManager?.queryIntentActivities(intent, 0)

        allApps?.sortBy { resolveInfo ->
            resolveInfo.loadLabel(packageManager).toString().toUpperCase()
        }
        return allApps
    }

    private fun setRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.appList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AppListAdapter(items)

        val fastScroller = view.findViewById<RecyclerViewFastScroller>(R.id.fastScroller)
        fastScroller.setUpAlphabet(alphabet)
        fastScroller.setRecyclerView(recyclerView)
    }

    fun setData(allApps: List<ResolveInfo>) {
        val packageManager = activity?.packageManager
        var headingLetter: String? = null
        allApps.forEachIndexed { i, resolveInfo ->
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)

            val labelFirstLetter = label[0].toUpperCase().toString();

            if (!labelFirstLetter.isDigitsOnly() && headingLetter != labelFirstLetter) {
                headingLetter = labelFirstLetter;
                items.add(AppListItem(labelFirstLetter, null, ShapeDrawable(), true))
                alphabet.add(AlphabetItem(i, labelFirstLetter, false))
            }

            val packageName = resolveInfo.activityInfo.packageName
            val appListItem = AppListItem(label, packageName, icon, false)
            items.add(appListItem)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val preferencesName = Constants.SHARED_PREFERENCES_NAME.value
        val preferencesKey = Constants.SHARED_PREFERENCES_HOME_APPS_KEY.value

        val gson = Gson()

        val sharedPreferences = activity?.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val appsSerialized = sharedPreferences?.getString(preferencesKey, "[]")

        val typeToken = TypeToken.getParameterized(MutableList::class.java, HomeListItem::class.java)
        var homeApps = gson.fromJson<MutableList<HomeListItem>>(appsSerialized, typeToken.type)

        if (item.groupId != ContextMenuGroup.ADD_TO_HOME.value) {
            homeApps = homeApps.filterIndexed { index, _ -> index != item.itemId }.toMutableList()
        } else {
            val appListItem = items[item.itemId]
            val newItem = HomeListItem(appListItem.label, appListItem.packageName)
            homeApps.add(newItem)
        }

        activity?.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)?.edit {
            putString(preferencesKey, gson.toJson(homeApps))
            commit()
        }

        val intent = Intent(Constants.REFRESH_HOME_INTENT.value)
        activity?.sendBroadcast(intent)

        return true
    }
}