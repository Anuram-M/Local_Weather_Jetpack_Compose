package com.ram.local_weather.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class IconChangerWorker(val context: Context, val parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        Log.d("WORKICON", "doWork: before changing")
        IconUtil().switchAppIcon(context, true)
        Log.d("WORKICON", "doWork: after changing")
        return Result.success()
    }

}