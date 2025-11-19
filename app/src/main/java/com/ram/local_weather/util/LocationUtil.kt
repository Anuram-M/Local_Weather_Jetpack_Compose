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
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume


class LocationUtil @Inject constructor(
    val fusedLocationProviderClient: FusedLocationProviderClient
) {

    lateinit var location: Location
    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    suspend fun getLocationDetails(): Location? = suspendCancellableCoroutine { cont ->
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).setMaxUpdates(1).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    Log.d("WORKWORK", "onLocationResult: 12345 : ${result.lastLocation}")
                    location = it
                    cont.resume(it)
                    fusedLocationProviderClient.removeLocationUpdates(this)
                } ?: cont.resume(null)
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        cont.invokeOnCancellation {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }


}