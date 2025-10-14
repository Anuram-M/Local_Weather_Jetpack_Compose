package com.ram.local_weather.screens

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.ram.local_weather.viewmodels.LocationViewModel

@Composable
fun LocationToggleComposable(context: Context, locationViewModel: LocationViewModel) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                locationViewModel.checkAppState()
            }
        }
    }
    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build()

        val locationBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val service = LocationServices.getSettingsClient(context)

        val task = service.checkLocationSettings(locationBuilder)

        task.addOnSuccessListener {
            locationViewModel.updatePermission(true)
            locationViewModel.checkAppState()
        }
        task.addOnFailureListener({ e ->
            if (e is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(e.resolution).build()
                    launcher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
            Toast.makeText(context, "Location Not Enabled", Toast.LENGTH_SHORT).show()
        })
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Location screen")
    }
}