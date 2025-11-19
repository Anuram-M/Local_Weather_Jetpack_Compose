package com.ram.local_weather.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

class IconUtil {

    fun switchAppIcon(context: Context, useChristmasIcon: Boolean) {
        val pm = context.packageManager
        val defaultIcon = ComponentName(context, "${context.packageName}.DefaultIcon")
        val christmasIcon =
            ComponentName(context, "${context.packageName}.ChristmasIcon")
        if (useChristmasIcon) {
            pm.setComponentEnabledSetting(
                christmasIcon,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            pm.setComponentEnabledSetting(
                defaultIcon,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } else {
            pm.setComponentEnabledSetting(
                defaultIcon,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            pm.setComponentEnabledSetting(
                christmasIcon,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}