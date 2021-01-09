package com.minlauncher.min.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.minlauncher.min.R

class SettingsImageSwitch(private val src: Int) : Fragment() {

    private var imageView: ImageView? = null
    private var checkView: ImageView? = null
    private var isChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_image_switch, container, false)

        imageView = view.findViewById(R.id.settingsSwitchImage)
        imageView?.setImageDrawable(context?.getDrawable(src))

        checkView = view.findViewById(R.id.settingsSwitchSwitch)
        isActive(isChecked)

        return view
    }

    fun isActive(isActive: Boolean) {
        isChecked = isActive
        checkView?.visibility = if (isActive) View.VISIBLE else View.GONE
    }
}