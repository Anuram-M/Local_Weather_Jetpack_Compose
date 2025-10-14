package com.ram.local_weather.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ram.local_weather.viewmodels.LocationViewModel


@Composable
fun PermissionScreenComposable(locationViewModel: LocationViewModel) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissios ->
        when{
            permissios.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)
                   locationViewModel.checkAppState()
            }
            permissios.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)
               locationViewModel.checkAppState()
            }
            else -> {

            }
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "This app requires location permission to work properly", textAlign = TextAlign.Center)
            Button(onClick = {
                launcher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }) {
                Text("Provide Permission")
            }
        }

    }
}