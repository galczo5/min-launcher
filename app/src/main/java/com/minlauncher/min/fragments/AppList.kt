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
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minlauncher.min.adapters.AppListAdapter
import com.minlauncher.min.R
import com.minlauncher.min.models.AppListItem
import com.minlauncher.min.models.ContextMenuGroup
import com.minlauncher.min.models.HomeListItem
import com.viethoa.RecyclerViewFastScroller
import com.viethoa.models.AlphabetItem

class AppList : Fragment() {

    var items = mutableListOf<AppListItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)

        val packageManager = activity?.getPackageManager()

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allApps = packageManager?.queryIntentActivities(intent, 0)

        allApps?.sortBy { resolveInfo -> resolveInfo.loadLabel(packageManager).toString().toUpperCase() }

        items = mutableListOf<AppListItem>()
        val alphabet = mutableListOf<AlphabetItem>()

        var headingLetter: String? = null
        allApps?.forEachIndexed { i, resolveInfo ->
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)

            val labelFirstLetter = label[0].toUpperCase().toString();

            if (headingLetter != labelFirstLetter) {
                headingLetter = labelFirstLetter;
                items.add(AppListItem(labelFirstLetter, null, ShapeDrawable(), true))
                alphabet.add(AlphabetItem(i, labelFirstLetter, false))
            }

            val packageName = resolveInfo.activityInfo.packageName
            val appListItem = AppListItem(label, packageName, icon, false)
            items.add(appListItem)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.appList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = items?.let { AppListAdapter(items) }

        val fastScroller = view.findViewById<RecyclerViewFastScroller>(R.id.fastScroller)
        fastScroller.setUpAlphabet(alphabet)
        fastScroller.setRecyclerView(recyclerView)

        return view
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val appsSerialized = activity?.getSharedPreferences("HOME", Context.MODE_PRIVATE)?.getString("HOME_APPS", "[]")
        val gson = Gson()

        val typeToken = TypeToken.getParameterized(MutableList::class.java, HomeListItem::class.java)
        var homeApps = gson.fromJson<MutableList<HomeListItem>>(appsSerialized, typeToken.type)

        if (item.groupId != ContextMenuGroup.ADD_TO_HOME.value) {
            homeApps = homeApps.filterIndexed { index, _ -> index != item.itemId }.toMutableList()
        } else {
            val appListItem = items[item.itemId]
            val newItem = HomeListItem(appListItem.label, appListItem.packageName)
            homeApps.add(newItem)
        }

        activity?.getSharedPreferences("HOME", Context.MODE_PRIVATE)?.edit {
            putString("HOME_APPS", gson.toJson(homeApps))
            commit()
        }

        activity?.sendBroadcast(Intent("REFRESH_HOME_SCREEN"))

        return true
    }
}