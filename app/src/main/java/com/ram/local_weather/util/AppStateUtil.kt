//package com.ram.local_weather.util
//
//import android.content.Context
//
//class AppStateUtil {
//
//    fun IsAppInForeground(context: Context) : Boolean {
//        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
//        val process = activityManager.runningAppProcesses ?: return  false
//        val packageName = context.packageName
//        process.forEach {
//            if(it.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && it.processName == packageName) {
//                return  true
//            }
//        }
//        return  false
//    }
//
//}