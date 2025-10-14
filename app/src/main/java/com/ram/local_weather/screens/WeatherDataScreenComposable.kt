package com.ram.local_weather.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ram.local_weather.ForecastItemComposable
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.viewmodels.LocationViewModel
import kotlin.math.round

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherDataScreenComposable(
    locationViewModel: LocationViewModel
) {
    LaunchedEffect(Unit) {
        locationViewModel.getLocationUpdates()
    }
    val location by locationViewModel.location
    val isLoading by locationViewModel.isLoading
    val address by locationViewModel.address
    val weatherData by locationViewModel.weatherData
    val forecastData by locationViewModel.forecastData
    val context = LocalContext.current.applicationContext
    val mAddress = if (address != null) {
        "${address!!.subLocality}, ${address!!.locality}"
    } else {
        "address not found"
    }
    val textColor = Color.Black
    val canShowWeatherCard = weatherData != null

    var lastKnownState by remember { mutableStateOf<Boolean?>(null) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val isEnabled = CheckerUtil().checkLocationEnabled(context!!)
                    if(isEnabled != lastKnownState) {
                        lastKnownState = isEnabled
                        locationViewModel.checkAppState()
                        Toast.makeText(context, "toggle againn", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)

        onDispose { context.unregisterReceiver(receiver) }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())) {

            AnimatedVisibility(
                visible = canShowWeatherCard && address != null,

                ) {
                Card(
                    modifier = Modifier.padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE9E9E9)
                    ), elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(address!!.subLocality, fontSize = 24.sp, color = textColor, fontWeight = FontWeight.Bold)
                        Text(address!!.locality, fontSize = 18.sp, color = textColor)
                        Text(round( weatherData!!.main.temp).toInt().toString()+"º C", fontSize = 65.sp, fontWeight = FontWeight.Bold, color = textColor, modifier= Modifier.padding(top = 20.dp, bottom = 10.dp))
                        Text(weatherData!!.weather[0].description, color = textColor, fontSize = 20.sp,)
                        AsyncImage(
                            modifier = Modifier
                                .width(150.dp)
                                .height(150.dp),
                            model = "https://openweathermap.org/img/wn/${weatherData!!.weather[0].icon}@4x.png",
                            contentDescription = "Weather Icon",
                            contentScale = ContentScale.Fit
                        )
                    }

                }
            }

            AnimatedVisibility(
                visible = forecastData != null
            ) {
                LazyRow(modifier = Modifier.padding(horizontal = 5.dp)) {
                    items(forecastData!!.list) { item ->
                        ForecastItemComposable(item)
                    }
                }
            }

        }

    }
}