package com.ram.local_weather.util

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ram.core_domain.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WorkManagerUtil @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val repository: WeatherRepository,
    private val locationUtil: LocationUtil
) : CoroutineWorker(context, params) {


    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val location = locationUtil.getLocationDetails()
        if (location == null) {
            return Result.retry()
        }

        if (SharedPrefUtil.getBoolean(PREF_KEYS.ALREADY_SHOWN.name)) {
            return Result.success()
        }

        if (AppStateUtil().IsAppInForeground(context)) {
            return Result.success()
        }

        location.let {
            val result = repository.getWeatherData(
                it.latitude,
                it.longitude
            )
            val data = result.data
            NotificationUtil().sendWeatherNotification(
                context,
                data?.weather!![0].main,
                data?.main?.temp ?: 0.0,
                data?.wind?.speed ?: 0.0,
                data?.name ?: "Unknown"
            )
        }
        return Result.success()
    }
}