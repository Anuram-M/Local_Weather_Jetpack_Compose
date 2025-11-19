package com.ram.local_weather.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.viewmodels.LocationViewModel


@Composable
fun PermissionScreenComposable(locationViewModel: LocationViewModel) {

    val context = LocalContext.current.applicationContext
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissios ->
        when{
            permissios.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)
                   locationViewModel.checkAppState()
            }
            permissios.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)
               locationViewModel.checkAppState()
            }
            else -> {

            }
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if(granted) {
            Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
        }

    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
              painter = painterResource(R.drawable.permission_bg)  ,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                     Color.Black.copy(alpha = 0.8f),
                )
            )
        }

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(bottom = 20.dp, start = 10.dp, end = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
            Text(text = "This app requires location permission to work properly", textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(10.dp), color = Color.White)
            Button(onClick = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }, modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                Text("Location Permission", modifier = Modifier.padding(5.dp), fontSize = 16.sp)
            }

            Button(

                onClick = {
                notificationLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }, modifier = Modifier.fillMaxWidth().padding(5.dp)) {
                Text("Notification Permission",  modifier = Modifier.padding(5.dp), fontSize = 16.sp)
            }
        }

    }
}

//@Preview
//@Composable
//fun previewPermissionScreen() {
//    LocalWeatherTheme {
//        val viewModel : LocationViewModel = hiltViewModel()
//        PermissionScreenComposable(viewModel)
//    }
//}