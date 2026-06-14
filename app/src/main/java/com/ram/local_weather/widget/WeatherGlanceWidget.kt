package com.ram.local_weather.widget

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.glance.layout.Column
import androidx.glance.text.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import com.ram.local_weather.R

class WeatherGlanceWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {

            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val location = prefs[WidgetStateKeys.LOCATION_KEY] ?: "No-location"
            val temp = prefs[WidgetStateKeys.TEMPERATURE_KEY] ?: "--"
            val condition = prefs[WidgetStateKeys.WEATHER_CONDITION_KEY] ?: "Unknown"

            WeatherWidgetComposable(location, temp, condition)
        }
    }

    @Composable
    fun WeatherWidgetComposable(location: String, temp: String, condition: String) {


        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .cornerRadius(20.dp)
                .background(
                    color = Color(0xff4A90E2),
                ).padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                ) {
//                    Text(
//                        text = "Location: ",
//                        style = TextStyle(
//                            fontSize = 42.sp,
//                            fontWeight = FontWeight.Bold,
//
//                        )
//                    )

                    Image(
                        provider = ImageProvider(R.drawable.ping),
                        modifier = GlanceModifier.size(40.dp),
                        contentDescription = null
                    )
                    Spacer(
                        modifier = GlanceModifier.size(10.dp)
                    )
                    Text(
                        text = location,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(
                    modifier = GlanceModifier.fillMaxWidth().height(20.dp)
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                ) {
//                    Text(
//                        text = "Temperature: ",
//                        style = TextStyle(
//                            fontSize = 20.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
                    Image(
                        provider = ImageProvider(R.drawable.temp),
                        modifier = GlanceModifier.size(24.dp),
                        contentDescription = null
                    )
                    Spacer(
                        modifier = GlanceModifier.size(5.dp)
                    )
                    Text(
                        text = "${temp} ℃ / ${condition}",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
//                Row(
//                    modifier = GlanceModifier.fillMaxWidth(),
//                ) {
//                    Text(
//                        text = "Weather: ",
//                        style = TextStyle(
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    )
//                    Text(
//                        text = condition,
//                        style = TextStyle(
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Normal
//                        )
//                    )
//                }
            }
        }
    }
}