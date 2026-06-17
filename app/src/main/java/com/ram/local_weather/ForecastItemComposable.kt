package com.ram.local_weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.ram.core_database.dto.MappedForecast
import com.ram.local_weather.util.DateConvertor
import kotlin.math.round

@Composable
fun ForecastItemComposable(forecastItem: MappedForecast, spFont: FontFamily, modifier: Modifier) {
    val time = DateConvertor.getDateAndTime(forecastItem.dateInMillis).second
    val date = DateConvertor.getDayLabel(forecastItem.dateInMillis)
    val textColor = Color.Black
    Card(
        modifier = modifier
//            .width(150.dp)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp,),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                round(forecastItem.temp).toInt().toString() + " ℃",
                fontSize = 24.sp,
                modifier = Modifier.padding(5.dp),
                color = textColor,
                fontFamily = spFont
            )
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${forecastItem.icon}@2x.png",
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(60.dp)
            )

            Text(
                date,
                fontSize = 14.sp,
                color = textColor,
                fontFamily = spFont,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                time,
                fontSize = 14.sp,
                color = textColor,
                fontFamily = spFont
            )
        }

    }

}