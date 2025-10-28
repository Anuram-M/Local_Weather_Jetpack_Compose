package com.ram.local_weather.screens

import android.location.Address
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ram.local_weather.models.WeatherResponse
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.ui.theme.sarpanchFont
import kotlin.math.abs
import kotlin.math.round

@Composable
fun SearchWeatherDataScreen(weatherData: WeatherResponse?, address: Address?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 40.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE0E0E0)
            ), elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LocalityCard(address, sarpanchFont)

                SearchedWeather(weatherData, Color.Black, sarpanchFont)

            }

        }
    }
}

@Composable
fun SearchedWeather(weatherData: WeatherResponse?, textColor: Color, spFont: FontFamily) {
    AnimatedVisibility(visible = weatherData != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                round(weatherData!!.main.temp).toInt().toString() + "º C",
                fontSize = 65.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp),
                fontFamily = spFont
            )
            Text(
                weatherData!!.weather[0].description,
                color = textColor,
                fontSize = 20.sp,
                fontFamily = spFont
            )
            AsyncImage(
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp),
                model = "https://openweathermap.org/img/wn/${weatherData!!.weather[0].icon}@4x.png",
                contentDescription = "Weather Icon",
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.height(80.dp).weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "High",
                            fontSize = 16.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = weatherData!!.main.temp_max.toString(),
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Low",
                            fontSize = 16.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData?.main?.temp_min}",
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
                    modifier = Modifier.height(80.dp).weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Humidity",
                            fontSize = 16.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = weatherData!!.main.humidity.toString() + "%",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier.height(80.dp).weight(1f)
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Wind",
                            fontSize = 16.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "%.2f".format((weatherData?.wind?.speed ?: 0.0) * 3.6) + " km/h",
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

@Preview
@Composable
fun previewSearchData() {
    LocalWeatherTheme {
        SearchWeatherDataScreen(null, null)
    }
}