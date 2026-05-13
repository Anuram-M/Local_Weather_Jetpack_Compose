package com.ram.local_weather.util

import android.content.Context
import androidx.core.content.edit

class SharedPrefUtil {

    val PREF_NAME = "my_pref"
    val isFirstKey = "isFirst"
    fun isFirstTime(context: Context) : Boolean {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isFirst = sharedPref.getBoolean(isFirstKey, true)
        if(isFirst) {
            sharedPref.edit{ putBoolean(isFirstKey, false) }
        }
        return isFirst
    }
}