package com.ram.local_weather.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ram.core_database.dto.MappedWeather
import com.ram.local_weather.ForecastItemComposable
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.util.BackgroundSelectorUtil
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.util.WorkManagerUtil
import com.ram.local_weather.viewmodels.LocationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherHomeComposable(
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current.applicationContext

    val firebaseDatabase = Firebase.firestore
    var isRefreshing by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember {
        mutableStateOf(false)
    }

    var searchDefaultList by remember {
        mutableStateOf(listOf(""))
    }
    LaunchedEffect(Unit) {
        while (true) {
            firebaseDatabase.collection("location").get().addOnSuccessListener { result ->
                Log.d("RESTP", "WeatherDataScreenComposable: $result")
                for (item in result) {
                    searchDefaultList = item["city list"] as List<String>
                    Log.d("RESTP", "WeatherDataScreenComposable: ${item["city list"]}")
                }
            }.addOnFailureListener {

            }
            delay(60000)
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        locationViewModel.getLocationUpdates()
    }

//    LaunchedEffect(Unit) {
//        val periodicCheckRequest = PeriodicWorkRequestBuilder<IconChangerWorker>(2,TimeUnit.MINUTES).setInitialDelay(100, TimeUnit.SECONDS).build()
//
//        WorkManager.getInstance(context)
//            .enqueueUniquePeriodicWork("UNiq2", ExistingPeriodicWorkPolicy.KEEP, periodicCheckRequest)
//
//    }


    val weatherData by locationViewModel.mappedWeather.collectAsStateWithLifecycle()
    val savedweatherData by locationViewModel.savedWeather.collectAsStateWithLifecycle()
    val forecastData by locationViewModel.forecastData
    var searchWeatherData by locationViewModel.searchWeather
    val refreshCount by locationViewModel.refreshCount
    val textColor = Color.Black

    var lastKnownState by remember { mutableStateOf<Boolean?>(null) }
    var isSearchWeather by remember {
        mutableStateOf(false)
    }

    var mainBg by remember {
        mutableStateOf(Color.Gray)
    }
    var isDay by remember {
        mutableStateOf(true)
    }
    val animatedBackground by animateColorAsState(
        targetValue = mainBg,
        animationSpec = tween(2000)
    )
    val animatedDayAccent by animateColorAsState(
        targetValue = if (isDay) Color.Black else Color.Black,
        animationSpec = tween(2000)
    )
    var animationWidget by remember {
        mutableStateOf("")
    }

    var searchList by remember {
        mutableStateOf(listOf(""))
    }
    DisposableEffect(Unit) {

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val isEnabled = CheckerUtil().checkLocationEnabled(context!!)
                    if (isEnabled != lastKnownState) {
                        lastKnownState = isEnabled
                        locationViewModel.checkAppState()
                    }
                }
            }
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)

        onDispose { context.unregisterReceiver(receiver) }
    }


    LaunchedEffect(weatherData) {
        if (weatherData != null) {
//            animationWidget = weatherData?.weather!![0].main
            isLoading = false
            isRefreshing = false
        }
    }

    LaunchedEffect(searchWeatherData) {
        if (searchWeatherData != null) {
            isDay = searchWeatherData?.weather!![0].icon.endsWith('d')
            animationWidget = searchWeatherData?.weather!![0].main
            mainBg = BackgroundSelectorUtil().backgroundChoice(searchWeatherData?.weather!![0].id)
        }
        if (searchWeatherData == null && weatherData != null) {
//            animationWidget = weatherData?.weather!![0].main
            isDay = weatherData?.icon?.endsWith('d') == true
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherData?.weatherCategory!!)
        }
    }

    LaunchedEffect(refreshCount) {
        weatherData?.let {
            if (searchWeatherData == null) {
//                animationWidget = weatherData?.weather!![0].main
            }
            isDay = weatherData?.icon?.endsWith('d') == true
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherData?.weatherCategory!!)
        }
    }
    LaunchedEffect(Unit) {
        WorkManager.getInstance(context).cancelAllWork()
        val workRequest =
            PeriodicWorkRequestBuilder<WorkManagerUtil>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("UNiq", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(animatedBackground, animatedDayAccent)
                    )
                )
        )

//        if(!animationWidget.isNullOrEmpty()) {
//            when(animationWidget.lowercase()) {
//                "rain" -> RainAnimation(
//                    modifier = Modifier.fillMaxSize(),
//                    rainIntensity = 300, rainSpeed = 200f, angle = -10f
//                )
//                "clouds" -> MovingCloudsAnimation(
//                    modifier = Modifier.fillMaxSize(),
//                )
//                "snow" -> Snowfall()
//                "drizzle" -> RainAnimation(
//                    modifier = Modifier.fillMaxSize(),
//                    rainIntensity = 30, rainSpeed = 50f, angle = 0f
//                )
//            }
//        }
        var query by remember {
            mutableStateOf("")
        }
        var expand by remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.systemBarsPadding()) {
            NewSearchBar(
                query = query,
                onQueryChange = { query = it },
                expanded = expand,
                onExpandMChange = { expand = !expand },
                locationViewModel,
                isSearchWeather,
                onSearch = { isSearchWeather = true },
                onClear = {
                    isLoading = true
                    locationViewModel.getLocationUpdates()
                    locationViewModel.resetSearchWeather()
                },
                searchDefaultList,
                poppinsFont
            )
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        locationViewModel.getLocationUpdates()
                        delay(1500)
                        isRefreshing = false
                    }
                },
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = refreshState,
                        isRefreshing = isRefreshing,
                        containerColor = Color.Black,
                        color = Color.Red
                    )
                }
            ) {

                when {
                    isLoading || isRefreshing -> {
                        ShimmerPlaceholderComposable()
                    }

                    searchWeatherData != null -> {
                        QueryLocationWeatherComposable(searchWeatherData!!)
                    }

                    weatherData != null -> {
//                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(25.dp), contentAlignment = Alignment.Center) {
//                            Image(
//                                painter = painterResource(R.drawable.weather_empty_state),
//                                contentDescription = null
//                            )
//                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(10.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Card(
                                    modifier = Modifier
                                        .padding(10.dp),
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
                                        LocalityCard(
                                            Pair(
                                                weatherData?.locality,
                                                weatherData?.subLocality
                                            ), poppinsFont
                                        )
                                        WeatherDataCard(weatherData!!, textColor, poppinsFont)
                                    }
                                }

                                AnimatedVisibility(
                                    visible = forecastData != null,
                                ) {
                                    LazyRow(
                                        modifier = Modifier
                                            .padding(horizontal = 5.dp)
                                    ) {
                                        items(forecastData?.list!!) { item ->
                                            ForecastItemComposable(item, poppinsFont)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocalityCard(pair: Pair<String?, String?>, customFont: FontFamily) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = pair.second != null
        ) {
            pair.second?.let {
                Text(
                    it,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFont
                )
            }
        }
        AnimatedVisibility(
            visible = pair.first != null
        ) {
            pair.first?.let {
                Text(
                    it,
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontFamily = customFont
                )
            }
        }
    }
}

@Composable
fun WeatherDataCard(weatherData: MappedWeather, textColor: Color, spFont: FontFamily) {
    AnimatedVisibility(visible = weatherData != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    model = "https://openweathermap.org/img/wn/${weatherData.icon}@4x.png",
                    contentDescription = "Weather Icon",
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${weatherData.mainTemp.toInt()}ºC",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontFamily = spFont
                    )
                }
            }

            Text(
                weatherData.description!!,
                color = textColor,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = spFont
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "Low ↓",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData.minTemp.toInt()}ºC",
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(
                            text = "High ↑",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "${weatherData.maxTemp.toInt()}ºC",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 5.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Humidity",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = weatherData.humidity.toString() + "%",
                            fontSize = 14.sp,
                            color = textColor,
                            fontFamily = spFont
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFCFCFCF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(vertical = 5.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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