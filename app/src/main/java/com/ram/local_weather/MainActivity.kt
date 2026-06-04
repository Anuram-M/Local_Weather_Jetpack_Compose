 package com.ram.local_weather

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.ui.util.trace
import androidx.navigation.compose.rememberNavController
import com.ram.local_weather.screens.WeatherHomeScreen
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

 @AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        trace("Activity.onCreate") {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContent {
                trace("setContent") {
                    LocalWeatherTheme {
                        val navController = rememberNavController()
                        WeatherHomeScreen(navController)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

//    fun createNotificationChannel(context: Context) {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                "channel_id",
//                "MY_NOTIFICATION_CHANNEL",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "sample_description"
//                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
//            }
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }
}
