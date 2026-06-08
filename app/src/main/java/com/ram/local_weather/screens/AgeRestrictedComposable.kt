package com.ram.local_weather.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.viewmodels.LocationViewModel


@Composable
fun AgeRestrictedComposable(locationViewModel: LocationViewModel) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.caution_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(10.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Restricted",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontFamily = poppinsFont
                    )
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center,
                    text = "Your parent or guardian has blocked access to this application. If you have obtained permission, please ask them to unblock the app in their parental settings and tap 'Recheck' below.",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = poppinsFont
                    )
                )
                Spacer(
                    modifier = Modifier.size(20.dp)
                )

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff4A90E2)
                    ),
                    onClick = {
                        locationViewModel.initializeAgeSignalManager()
                    }
                ) {
                    Text(
                        text = "Recheck",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontFamily = poppinsFont
                        )
                    )
                }
                Spacer(
                    modifier = Modifier.size(20.dp)
                )
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff4A90E2)
                    ),
                    onClick = {
                        (context as? Activity?)?.finishAffinity()
                    }
                ) {
                    Text(
                        text = "Exit App",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontFamily = poppinsFont
                        )
                    )
                }
            }
        }
    }

}