package com.ram.local_weather

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.ram.local_weather.screens.WeatherHomeScreen
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalWeatherTheme {
                WeatherHomeScreen()
            }
        }
        createNotificationChannel(this)

    }

    fun createNotificationChannel(context: Context) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "channel_id",
                "MY_NOTIFICATION_CHANNEL",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "sample_description"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
