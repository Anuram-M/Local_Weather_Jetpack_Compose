package com.ram.local_weather.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.viewmodels.LocationViewModel


@Composable
fun NotificationComposable(locationViewModel: LocationViewModel, navHostController: NavHostController) {

    val notificationGranted by locationViewModel.notificationPermission.collectAsStateWithLifecycle()

    val notificationLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if(result) {
            locationViewModel.updateNotificationPermission(result)
        }
    }
    val context = LocalContext.current.applicationContext

    val activity = context as? Activity

    val alreadyAskedPermission by locationViewModel.alreadyAskedNotificationPermission.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver{ _, event ->
            if(event == Lifecycle.Event.ON_RESUME) {
              locationViewModel.fetchPermissionState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    NotificationComposableContent(
        context,
        notificationGranted,
        onNavigate = { route ->
            if(route.equals("back")) {
                navHostController.popBackStack()
            } else {
                navHostController.navigate(route)
            }
        },
        onRequestPermission = { change ->
            val showRationale =
                activity?.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                    ?: false
            Log.d("OCGAGE", "NotificationComposable: ${showRationale}, ${locationViewModel.checkNotificationPermission()}, ${change}")
            if (change) {
                if (!locationViewModel.checkNotificationPermission()) {
                    if(!alreadyAskedPermission) {
                        locationViewModel.updateAlreadyAsked()
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else if (showRationale) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                    } else {
                        locationViewModel.openAppSettings(context)
                    }
                }
            } else {
                locationViewModel.openAppSettings(context)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationComposableContent(
    context: Context,
    permissionGranted: Boolean,
    onNavigate: (String) -> Unit,
    onRequestPermission: (Boolean) -> Unit
) {
    val activity = context as? Activity
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                ),
                title = { Text("Notification") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigate("back")
                    }) {
                        Icon(
                            // AutoMirrored ensures the arrow flips correctly for RTL languages like Arabic
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xffBDC3C7), Color.Black),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(top = 60.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "App Update Notification",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = poppinsFont,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal
                            )
                        )
                        Switch(
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                uncheckedThumbColor = Color.Red
                            ),
                            onCheckedChange = { change ->
                               onRequestPermission(change)
                            },
                            checked = permissionGranted,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun previewNotification() {
    LocalWeatherTheme {
        NotificationComposableContent(
            LocalContext.current,
            true,
            onNavigate = {

            },
            onRequestPermission = {}
        )
    }
}