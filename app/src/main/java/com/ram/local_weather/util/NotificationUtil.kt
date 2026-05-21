//package com.ram.local_weather.util
//
//import android.Manifest
//import android.app.Notification
//import android.content.Context
//import android.graphics.BitmapFactory
//import androidx.annotation.RequiresPermission
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.core.content.ContextCompat
//import com.ram.core_domain.models.WeatherResponse
//import com.ram.local_weather.R
//
//
//class NotificationUtil {
//
//    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
//    fun sendNotification(context: Context, array: Array<String>, data: WeatherResponse) {
//
//
//        val notificationCompat = NotificationCompat.Builder(context, "channel_id")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentText("The Weather is ${array[0]} with wind speed of ${array[1]}")
//            .setContentTitle("Local Weather")
//            .setCategory(Notification.CATEGORY_MESSAGE)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .build()
//
//        val notificationManager = NotificationManagerCompat.from(context)
//        notificationManager.notify(1001, notificationCompat)
//
//    }
//
//    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
//    fun sendWeatherNotification(
//        context: Context,
//        condition: String,
//        temperature: Double,
//        windSpeed: Double,
//        locationName: String
//    ) {
//
//        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_foreground)
//        val notification =
//            NotificationCompat.Builder(context, "channel_id").setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setLargeIcon(largeIcon)
//                .setContentTitle("${"%.0f".format(temperature)}°C • ${condition.replaceFirstChar { it.uppercase() }}")
//                .setContentText("Wind: ${"%.1f".format(windSpeed)} km/h • $locationName").setStyle(
//                    NotificationCompat.BigTextStyle().bigText(
//                        "Current weather in $locationName: $condition, " + "temperature ${
//                            "%.0f".format(temperature)
//                        }°C, " + "wind speed ${"%.1f".format(windSpeed * 3.6)} km/h."
//                    )
//                ).setColor(ContextCompat.getColor(context, R.color.purple_200))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE).setAutoCancel(true)
//                .build()
//        NotificationManagerCompat.from (context).notify(1001, notification)
//    }
//
//}