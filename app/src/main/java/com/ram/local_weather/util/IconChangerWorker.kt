//package com.ram.local_weather.util
//
//import android.content.Context
//import android.util.Log
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//
//class IconChangerWorker(val context: Context, val parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
//    override suspend fun doWork(): Result {
//        IconUtil().switchAppIcon(context, true)
//        return Result.success()
//    }
//
//}