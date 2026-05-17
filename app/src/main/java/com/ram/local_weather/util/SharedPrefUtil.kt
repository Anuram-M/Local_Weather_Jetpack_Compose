package com.ram.local_weather.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPrefUtil {

    val PREF_NAME = "my_pref"
    val isFirstKey = "isFirst"
    lateinit var pref: SharedPreferences
    fun initiate(context: Context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveBoolean(key: String, status: Boolean){
        pref.edit { putBoolean(key, status) }
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }
}