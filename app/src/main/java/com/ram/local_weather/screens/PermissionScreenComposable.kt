package com.ram.local_weather.screens

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.viewmodels.LocationViewModel


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionScreenComposable(locationViewModel: LocationViewModel) {

    val context = LocalContext.current.applicationContext
    var showBackgroundPermissionDialog by remember {
        mutableStateOf(false)
    }
    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { result ->
        if(result) {
            locationViewModel.checkAppState()
            Log.d("PERMIA", "PermissionScreenComposable: ${result}")
        }

    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissios ->
        when{
            permissios.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)
                locationViewModel.checkAppState()
//                showBackgroundPermissionDialog = true
            }
            permissios.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)
                locationViewModel.checkAppState()
//                showBackgroundPermissionDialog = true
            }
            else -> {

            }
        }
    }


    if(showBackgroundPermissionDialog) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AlertDialog(
                    title = {
                        Text(
                            "Note",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontFamily = poppinsFont
                        )
                    },
                    text = {
                        Text(
                            "Local weather requires backgound location permission for the app to show hourly notifications. Do you wish to receive notifications of weather in your location?",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontFamily = poppinsFont
                        )
                    },
                    onDismissRequest = {

                    },
                    containerColor = Color.LightGray,
                    confirmButton = {
                        TextButton(onClick = {
//                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
//                                data = Uri.fromParts("package", context.packageName, null)
//                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            }
//                            context.startActivity(intent)
                            backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            showBackgroundPermissionDialog = false
                        }) { Text("Go To Settings", color = Color.Black) }

                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showBackgroundPermissionDialog = false
                            }
                        ) {
                            Text("Cancel", color = Color.Black)
                        }

                    },

                    )


            } else {
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    var notificationStatus by remember {
        mutableStateOf(CheckerUtil().checkNotificationPermission(context))
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if(granted) {
            notificationStatus = true
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                    )
            )
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(bottom = 20.dp, start = 10.dp, end = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
            Text(text = "This app requires location permission to work properly", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontFamily = poppinsFont, fontSize = 16.sp, modifier = Modifier.padding(10.dp), color = Color.White)
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                onClick = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )
                )
            }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)) {
                Text("Location Permission", color = Color.White, modifier = Modifier.padding(5.dp), fontSize = 16.sp, fontFamily = poppinsFont)
            }

//            Button(
//                enabled = !notificationStatus,
//                onClick = {
//                notificationLauncher.launch(
//                    Manifest.permission.POST_NOTIFICATIONS
//                )
//            }, modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(5.dp)) {
//                Text("Notification Permission",  modifier = Modifier.padding(5.dp), fontSize = 16.sp, fontFamily = poppinsFont)
//            }

        }

    }
}