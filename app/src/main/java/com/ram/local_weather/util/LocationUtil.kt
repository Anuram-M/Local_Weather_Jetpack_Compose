package com.ram.local_weather.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import javax.inject.Inject


class LocationUtil @Inject constructor(
    val fusedLocationProviderClient: FusedLocationProviderClient
) {

    lateinit var location: Location
    lateinit var locationCallback: LocationCallback


    @SuppressLint("MissingPermission")
    fun getLocationDetails(): Location? {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).setMaxUpdates(1).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    Log.d("UTILPOL", "onLocationResult: 12345 : ${result.lastLocation}")
                    location = it
                    return@let location
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        return null
    }


}