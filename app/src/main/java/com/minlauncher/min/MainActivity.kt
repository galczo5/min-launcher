package com.minlauncher.min

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.edit
import androidx.viewpager2.widget.ViewPager2
import com.minlauncher.min.adapters.MainActivityScreensAdapter

class MainActivity : AppCompatActivity() {

    private val darkModeOnBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            saveDarkMode(MODE_NIGHT_YES)
        }
    }

    private val darkModeOffBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            saveDarkMode(MODE_NIGHT_NO)
        }
    }

    private fun saveDarkMode(mode: Int) {
        getSharedPreferences(Constants.DARK_MODE_SHARED_PREFERENCES_NAME.value, Context.MODE_PRIVATE).edit {
            putInt(Constants.DARK_MODE_SHARED_PREFERENCES_KEY.value, mode)
            commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityProperties()

        setContentView(R.layout.activity_main)

        registerReceiver(darkModeOnBroadcastReceiver, IntentFilter(Constants.DARK_MODE_ON.value))
        registerReceiver(darkModeOffBroadcastReceiver, IntentFilter(Constants.DARK_MODE_OFF.value))

        val sharedPreferences = getSharedPreferences(
            Constants.DARK_MODE_SHARED_PREFERENCES_NAME.value,
            Context.MODE_PRIVATE
        )

        val darkModeOn = sharedPreferences.getInt(Constants.DARK_MODE_SHARED_PREFERENCES_KEY.value, MODE_NIGHT_NO)
        AppCompatDelegate.setDefaultNightMode(darkModeOn)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = MainActivityScreensAdapter(supportFragmentManager, lifecycle)

    }

    fun setActivityProperties() {
        // Hide top bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()?.hide()
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Hide navigation
        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}