package com.ram.local_weather.screens

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

    val subscriptionStatus by locationViewModel.subscriptionStatus.collectAsStateWithLifecycle()

    var showDialog by remember {
        mutableStateOf(false)
    }
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
        notificationGranted,
        subscriptionStatus,
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
                        showDialog = true
                    }
                }
            } else {
                showDialog = true
            }
        },
        showDialog,
        onDismiss = {
            showDialog = !showDialog
        },
        onConfirm = {
            locationViewModel.openAppSettings(context)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationComposableContent(
    permissionGranted: Boolean,
    subscriptionStatus: String,
    onNavigate: (String) -> Unit,
    onRequestPermission: (Boolean) -> Unit,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

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
                                checkedTrackColor = Color(0xff4A90E2),
                                uncheckedThumbColor = Color.Red,
                                uncheckedTrackColor = Color.Gray
                            ),
                            onCheckedChange = { change ->
                               onRequestPermission(change)
                            },
                            checked = permissionGranted,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (subscriptionStatus.equals("Subscribed"))
                                "Receive notification for app updates"
                            else "Turned off, you won't be notified for updates.",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontFamily = poppinsFont,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }

                if(showDialog) {
                    BasicAlertDialog (
                        onDismissRequest = {
                            onDismiss()
                        },
                        content = {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xffBDC3C7)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (subscriptionStatus.equals("Subscribed"))
                                            "Turn off update notifications?"
                                        else
                                            "Turn on update notifications?",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            color = Color.Black,
                                            fontFamily = poppinsFont,
                                            fontWeight = FontWeight.Bold
                                        ))
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = if (subscriptionStatus.equals("Subscribed"))
                                            "You will stop receiving alerts when new features, bug fixes, or weather updates become available."
                                        else
                                            "It looks like you want to receive notifications for app updates.",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.Black,
                                            fontFamily = poppinsFont,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )

                                    Spacer(
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Cancel",
                                            modifier = Modifier.clickable{
                                                onDismiss()
                                            }, style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.DarkGray,
                                            fontFamily = poppinsFont,
                                            fontWeight = FontWeight.Normal
                                        ))
                                        Spacer(
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text("Confirm",
                                            modifier = Modifier.clickable{
                                                onConfirm()
                                                onDismiss()
                                            }, style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.Black,
                                            fontFamily = poppinsFont,
                                            fontWeight = FontWeight.Bold
                                        ))
                                    }
                                }
                            }
                        }
                    )
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
            true,
            "Subscribed",
            onNavigate = {},
            onRequestPermission = {},
            true,
            onDismiss = {},
            onConfirm = {}
        )
    }
}