package com.ram.local_weather.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ram.core_database.dto.MappedWeather
import com.ram.local_weather.ui.theme.poppinsFont
import kotlin.math.round

@Composable
fun QueryLocationWeatherComposable(weatherData: MappedWeather) {
    Log.d(
        "DPDPDPDP",
        "QueryLocationWeatherComposable: ${LocalConfiguration.current.screenWidthDp.dp}"
    )
    val config = LocalConfiguration.current
    val screenDp = remember {
        config.screenWidthDp.dp
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 20.dp)
                .wrapContentHeight()
                .heightIn(max = 400.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE0E0E0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            if (screenDp < 450.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    SearchLocalityCard(weatherData, Color.Black, poppinsFont)

                    SearchedWeather(weatherData, Color.Black, poppinsFont)

                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        SearchLocalityCard(weatherData, Color.Black, poppinsFont)
                    }

                    Box(
                        modifier = Modifier.weight(3f)
                    ) {

                        SearchedWeather(weatherData, Color.Black, poppinsFont)
                    }

                }
            }

        }
    }
}

@Composable
fun SearchedWeather(weatherData: MappedWeather, textColor: Color, spFont: FontFamily) {
    AnimatedVisibility(visible = weatherData != null) {
        Column(
            modifier = Modifier.wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxHeight()
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "Low ↓",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData?.minTemp} ºC",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxHeight()
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "High ↑",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData?.maxTemp} ºC",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxHeight().weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxHeight()
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "Humidity",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData?.humidity}%",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxHeight()
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "Wind",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData?.windSpeed} km/h",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchLocalityCard(weatherData: MappedWeather?,textColor: Color, spFont: FontFamily) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = weatherData?.locality != null
        ) {
            weatherData?.locality?.let {
                Text(
                    weatherData?.locality!!,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = spFont
                )
            }

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                modifier = Modifier
                    .size(84.dp),
                model = ImageRequest.Builder(LocalContext.current).data("https://openweathermap.org/img/wn/${weatherData!!.icon}@4x.png")
                    .size(coil.size.Size.ORIGINAL)
                    .crossfade(true)
                    .build(),
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Fit
            )
            Text(
                round(weatherData!!.mainTemp).toInt().toString() + " ℃",
                fontSize = 57.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(top = 10.dp, bottom = 0.dp),
                fontFamily = spFont
            )
        }

        Text(
            weatherData?.description!!,
            color = textColor,
            fontSize = 18.sp,
            fontFamily = spFont
        )


    }
}