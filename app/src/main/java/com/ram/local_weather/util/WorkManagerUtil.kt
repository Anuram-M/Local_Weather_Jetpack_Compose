package com.ram.local_weather.util

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ram.local_weather.repository.WeatherRepository
import com.ram.local_weather.viewmodels.LocationViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class WorkManagerUtil @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val repository: WeatherRepository,
    val locationUtil: LocationUtil
) : CoroutineWorker(context, params) {


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val location = locationUtil.getLocationDetails()
        location?.let {
            val result = repository.getWeatherData(
                it.latitude,
                it.longitude
            )
            val data = result.data
            NotificationUtil().sendNotification(
                context,
                arrayOf(data?.weather!![0].description, "%.2f".format((data.wind.speed ?: 0.0) * 3.6))
            )
            Log.d("WORKWORK", "doWork: $data")
        }

        return Result.success()
    }
}