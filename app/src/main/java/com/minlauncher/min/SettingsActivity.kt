package com.minlauncher.min

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minlauncher.min.adapters.HiddenAppListAdapter
import com.minlauncher.min.adapters.HiddenAppOnClickListener
import com.minlauncher.min.models.AppInfo
import com.minlauncher.min.models.AppInfoSharedPreferences


class SettingsActivity : AppCompatActivity() {

    var appInfoSharedPreferences: AppInfoSharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityProperties()
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_APPS.value, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            appInfoSharedPreferences = AppInfoSharedPreferences(sharedPreferences)
        }

        appInfoSharedPreferences?.getHiddenApps()?.let {
            val recyclerView = findViewById<RecyclerView>(R.id.hiddenAppsList)

            recyclerView.adapter = HiddenAppListAdapter(it, object : HiddenAppOnClickListener {
                override fun onClick(position: Int) {
                    val appInfo = it[position]
                    appInfoSharedPreferences?.update(AppInfo(appInfo.label, appInfo.packageName, appInfo.home, false))
                }
            })

            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        }

    }

    fun setActivityProperties() {
        // Hide top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


}