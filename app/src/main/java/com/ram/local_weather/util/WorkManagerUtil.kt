package com.ram.local_weather.util

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ram.local_weather.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WorkManagerUtil @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val repository: WeatherRepository,
    val locationUtil: LocationUtil
) : CoroutineWorker(context, params) {


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        Log.d("WORKWORK", "doWork: before calling")
        val location = locationUtil.getLocationDetails()
        Log.d("WORKWORK", "doWork: ${location}")
        if (location == null) {
            return Result.retry()
        }
        location.let {
            val result = repository.getWeatherData(
                it.latitude,
                it.longitude
            )
            val data = result.data
//            NotificationUtil().sendNotification(
//                context,
//                arrayOf(
//                    data?.weather!![0].description,
//                    "%.2f".format((data.wind.speed ?: 0.0) * 3.6)
//                ),
//                data
//            )
            NotificationUtil().sendWeatherNotification(
                context,
                data?.weather!![0].main,
                data?.main?.temp ?: 0.0,
                data?.wind?.speed ?: 0.0,
                data?.name ?: "Unknown"
            )
            Log.d("WORKWORK", "doWork: $data")
        }
        return Result.success()
    }
}