package com.ram.local_weather.util

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ram.local_weather.R


class NotificationUtil {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendNotification(context: Context, array: Array<String>) {

        val notificationCompat = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("The Weather is ${array[0]} with wind speed of ${array[1]}")
            .setContentTitle("Local Weather")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationCompat)

    }

}