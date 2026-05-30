package com.ram.local_weather.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ram.core_database.dto.MappedWeather
import com.ram.core_domain.models.WeatherResponse
import com.ram.local_weather.ui.theme.poppinsFont
import kotlin.math.round

@Composable
fun QueryLocationWeatherComposable(weatherData: MappedWeather?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 40.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE0E0E0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                SearchLocalityCard(weatherData, poppinsFont)

                SearchedWeather(weatherData, Color.Black, poppinsFont)

            }

        }
    }
}

@Composable
fun SearchedWeather(weatherData: MappedWeather?, textColor: Color, spFont: FontFamily) {
    AnimatedVisibility(visible = weatherData != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                round(weatherData!!.mainTemp).toInt().toString() + "º C",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                fontFamily = spFont
            )
            Text(
                weatherData.description!!,
                color = textColor,
                fontSize = 18.sp,
                fontFamily = spFont
            )
            AsyncImage(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                model = "https://openweathermap.org/img/wn/${weatherData!!.icon}@4x.png",
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Fit
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 5.dp)) {
                        Text(
                            text = "Low ↓",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData.minTemp} ºC",
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
                        .weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 5.dp)) {
                        Text(
                            text = "High ↑",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData.maxTemp} ºC",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 5.dp)) {
                        Text(
                            text = "Humidity",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData.humidity}%",
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
                        .weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 5.dp)) {
                        Text(
                            text = "Wind",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData.windSpeed} km/h",
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
fun SearchLocalityCard(address: MappedWeather?, spFont: FontFamily) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = address?.locality != null
        ) {
            address?.locality?.let {
                Text(
                    address?.locality!!,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = spFont
                )
            }

        }
    }
}