package com.ram.local_weather.screens

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.viewmodels.LocationViewModel

@Composable
fun LocationToggleComposable(
    context: Context,
    locationViewModel: LocationViewModel,
    navController: NavHostController
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                locationViewModel.checkAppState()
            }
        }
    }


    val bgBrush = remember {
        Brush.linearGradient(listOf(Color.White, Color.Gray, Color.Black))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = bgBrush
            )
            .padding(0.dp), contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.height(300.dp), contentAlignment = Alignment.Center) {

            Image(
                painter = painterResource(R.drawable.location_off),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(50.dp)
            )
            rippleEffect()
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(bottom = 20.dp, start = 10.dp, end = 10.dp)
        ) {
            Text(
                "The app requires GPS to fetch the current location data!",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = poppinsFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                onClick = {
                    showLocationToggle(context, locationViewModel, launcher)
                }) {
                Text("Turn on", modifier = Modifier.padding(3.dp), fontSize = 16.sp, color = Color.White, fontFamily = poppinsFont)
            }
            TextButton(
                onClick = {
                    navController.navigate("weather") {
                        popUpTo(0)
                    }
                },
            ) {
                Text(
                    text = "Skip for now",
                    modifier = Modifier.padding(3.dp),
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = poppinsFont
                )
            }
        }

    }
}

@Composable
fun rippleEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_anim")

    val radius = infiniteTransition.animateFloat(
        initialValue = 80f,
        targetValue = 400f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(250.dp)
            .clip(CircleShape)
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        radius = radius.value,
                        color = Color.Black.copy(alpha = alpha.value),
                        style = Stroke(width = 80f),
                    )
                }
            }
    )
}


fun showLocationToggle(
    context: Context,
    locationViewModel: LocationViewModel,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L).build()

    val locationBuilder =
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
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
    })
}