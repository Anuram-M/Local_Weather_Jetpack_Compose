package com.ram.local_weather.widget

import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetStateKeys {
    val LOCATION_KEY = stringPreferencesKey("location_name")
    val TEMPERATURE_KEY = stringPreferencesKey("temperature_value")
    val WEATHER_CONDITION_KEY = stringPreferencesKey("weather_condition")
}