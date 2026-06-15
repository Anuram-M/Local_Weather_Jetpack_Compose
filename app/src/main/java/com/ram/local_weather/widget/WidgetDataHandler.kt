package com.ram.local_weather.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class WidgetDataHandler @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun updateWidgetData(
        location: String,
        temp: String,
        condition: String
    ) {

        val glanceManager = GlanceAppWidgetManager(context)
        val glanceIds = glanceManager.getGlanceIds(WeatherGlanceWidget::class.java)

        glanceIds.forEach { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                val mprefs = prefs.toMutablePreferences()
                mprefs[WidgetStateKeys.LOCATION_KEY] = location
                mprefs[WidgetStateKeys.TEMPERATURE_KEY] = temp
                mprefs[WidgetStateKeys.WEATHER_CONDITION_KEY] = condition

                mprefs
            }
            WeatherGlanceWidget().update(context, id)
        }
    }
}