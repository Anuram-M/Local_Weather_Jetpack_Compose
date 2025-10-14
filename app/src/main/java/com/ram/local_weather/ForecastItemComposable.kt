package com.ram.local_weather

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ram.local_weather.models.ForecastItem
import com.ram.local_weather.util.DateConvertor
import kotlin.math.round

@Composable
fun ForecastItemComposable(forecastItem: ForecastItem) {
    val time = DateConvertor().getDateAndTime(forecastItem.dt).second
    val date = DateConvertor().getDayLabel(forecastItem.dt)
    val textColor = Color.Black
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E9E9))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                round(forecastItem.main.temp).toInt().toString() + "º C",
                fontSize = 30.sp,
                modifier = Modifier.padding(10.dp),
                color = textColor
            )
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${forecastItem.weather[0].icon}@2x.png",
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .padding(10.dp)
            )

            Text(
                date,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 5.dp),
                color = textColor
            )
            Text(
                time,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 10.dp),
                color = textColor
            )
        }

    }

}