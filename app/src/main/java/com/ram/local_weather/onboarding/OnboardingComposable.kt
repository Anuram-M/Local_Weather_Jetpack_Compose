package com.ram.local_weather.onboarding

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.util.PREF_KEYS
import com.ram.local_weather.util.SharedPrefUtil
import com.ram.local_weather.viewmodels.LocationViewModel

@Composable
fun OnboardingComposable(locationViewModel: LocationViewModel) {

    val slides = listOf("first", "second", "third")
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val onboardingSlide = listOf(
        OnboardingModel(
            heading = "Current Weather",
            content = "Check the current weather of your location at ease."
        ),
        OnboardingModel(
            heading = "5-day Forecast",
            content = "Forecast of next 5-days are available with an interval of 3-hours."
        ),
        OnboardingModel(
            heading = "Check remote Location",
            content = "You can also check the weather of a remote location by searching with city or state names."
        ),
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(R.drawable.day),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.4f)
                    .safeContentPadding()
                    .background(shape = RoundedCornerShape(10), color = Color.White),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.matchParentSize()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = onboardingSlide[page].heading,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 40.sp,
                                fontSize = 32.sp,
                                fontFamily = poppinsFont
                            )
                            Text(
                                text = onboardingSlide[page].content,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                fontFamily = poppinsFont
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 80.dp, start = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(slides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 10.dp else 6.dp) // Makes the active dot slightly larger
                                .background(
                                    color = if (isSelected) Color(0xFF007AFF) else Color.LightGray,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                onClick = {
                    SharedPrefUtil.saveBoolean(PREF_KEYS.ALREADY_ONBOARDED.name, true)
                    locationViewModel.checkAppState()
                }, modifier = Modifier
                    .widthIn(min = 250.dp, max = 600.dp)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Let's check the sky ->",
                    color = Color.White,
                    modifier = Modifier.padding(5.dp),
                    fontSize = 16.sp,
                    fontFamily = poppinsFont
                )
            }
        }
    }
}