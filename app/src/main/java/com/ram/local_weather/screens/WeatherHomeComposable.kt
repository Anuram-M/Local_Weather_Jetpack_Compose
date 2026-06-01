package com.ram.local_weather.screens

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.ram.core_database.dto.MappedWeather
import com.ram.local_weather.ForecastItemComposable
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.util.BackgroundSelectorUtil
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.viewmodels.LocationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherHomeComposable(
    locationViewModel: LocationViewModel,
    navController: NavHostController
) {
    // 1. Declare a static class wrapper or an anonymous object tracker
    val tracker = remember { object { var count = 0 } }
    tracker.count++

    // Log directly during composition execution
    Log.d("COMPOSE", "WeatherScreen recomposed: ${tracker.count}")
    val context = LocalContext.current.applicationContext

    val checkerUtil by remember {
        mutableStateOf(CheckerUtil(context))
    }
    var isRefreshing by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()

    val toggleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {  result ->
        run {
            if (result.resultCode == Activity.RESULT_OK) {
                locationViewModel.getLocationUpdates(context)
            }
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissios ->
        when{
            permissios.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ||
                    permissios.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationViewModel.updatePermission(true)

                showLocationToggleH(context, locationViewModel, toggleLauncher)
            }
        }
    }

    LaunchedEffect(Unit) {
        locationViewModel.getLocationUpdates(context)
    }


//    val weatherData by locationViewModel.mappedWeather.collectAsStateWithLifecycle()
//    val isLoading by locationViewModel.isLoading.collectAsStateWithLifecycle()
//    val forecastData by locationViewModel.forecastData.collectAsStateWithLifecycle()
//    val searchWeatherData by locationViewModel.searchWeather.collectAsStateWithLifecycle()
    val weatherUIState by locationViewModel.weatherDataUI.collectAsStateWithLifecycle()
    val textColor = Color.Black
    val refreshCount by locationViewModel.refreshCount
    val initialLocationEnabled = checkerUtil.checkLocationEnabled()    // one call

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

    var pingIcon by remember {
        mutableStateOf(
            if (initialLocationEnabled) R.drawable.on_location
            else R.drawable.location_off
        )
    }
    var isLocationEnabled by remember { mutableStateOf(initialLocationEnabled) }
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val isEnabled = checkerUtil.checkLocationEnabled()
                    if (isEnabled != isLocationEnabled) {
                        isLocationEnabled = isEnabled
                        locationViewModel.stopLocationUpdate()
                        pingIcon = if(isEnabled) R.drawable.on_location else R.drawable.location_off
                    }
                }
            }
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, filter)

        onDispose { context.unregisterReceiver(receiver) }
    }


    LaunchedEffect(weatherUIState.weatherData) {
        if (weatherUIState.weatherData != null) {
            isRefreshing = false
            isDay = weatherUIState.weatherData?.icon?.endsWith('d') == true
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherUIState.weatherData?.weatherCategory!!)
        }
    }

    LaunchedEffect(weatherUIState.searchWeather) {
        if (weatherUIState.searchWeather != null) {
            isDay = weatherUIState.searchWeather?.icon?.endsWith('d') == true
//            animationWidget = weatherUIState.searchWeather?.main
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherUIState.searchWeather?.weatherCategory!!)
        }
        if (weatherUIState.searchWeather == null && weatherUIState.weatherData != null) {
//            animationWidget = weatherData?.weather!![0].main
            isDay = weatherUIState.weatherData?.icon?.endsWith('d') == true
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherUIState.weatherData?.weatherCategory!!)
        }
    }

    LaunchedEffect(refreshCount) {
        weatherUIState.weatherData?.let {
//            if (searchWeatherData == null) {
////                animationWidget = weatherData?.weather!![0].main
//            }
            isDay = weatherUIState.weatherData?.icon?.endsWith('d') == true
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherUIState.weatherData?.weatherCategory!!)
        }
    }

//    LaunchedEffect(Unit) {
//        WorkManager.getInstance(context).cancelAllWork()
//        val workRequest =
//            PeriodicWorkRequestBuilder<WorkManagerUtil>(15, TimeUnit.MINUTES).build()
//        WorkManager.getInstance(context)
//            .enqueueUniquePeriodicWork("UNiq", ExistingPeriodicWorkPolicy.KEEP, workRequest)
//    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.White,
                shape = CircleShape,
                onClick = {
                    if(!checkerUtil.checkLocationPermission()) {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else if(!checkerUtil.checkLocationEnabled()) {
                        showLocationToggleH(context, locationViewModel, toggleLauncher)
                    }
                }
            ) {
                Icon(painter = painterResource(pingIcon), contentDescription = null, tint = Color.Black, modifier = Modifier.size(30.dp))
            }
        }

    ) { innerPadding ->
        Log.d("BDC", "WeatherHomeComposable: ${mainBg.value}, ${isDay}")
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
                Row(
                   modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        NewSearchBar(
                            query = query,
                            onQueryChange = { query = it },
                            expanded = expand,
                            onExpandChange = { expand = !expand },
                            locationViewModel,
                            onClear = {
                                locationViewModel.resetSearchWeather()
                                locationViewModel.getLocationUpdates(context)
                            },
                            poppinsFont,
                            navController
                        )
                    }
                    AnimatedVisibility(
                        visible = !expand,
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .size(40.dp)
                                .weight(1f),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0E0E0)
                            )
                        ) {
                            IconButton(
                                onClick = {
                                    navController.navigate("history")
                                }
                            ) {
                                Icon(painter = painterResource(R.drawable.history), tint = Color.Black, contentDescription = null, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
                PullToRefreshBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter,
                    state = refreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        coroutineScope.launch {
                            isRefreshing = true
                            locationViewModel.getLocationUpdates(context)
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
//                        isLoading || isRefreshing
                        weatherUIState.isLoading -> {
                            ShimmerPlaceholderComposable()
                        }

                        !weatherUIState.error.isNullOrEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    text = weatherUIState.error!!,
                                    style = TextStyle(
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        }

                        weatherUIState.isCurrent -> {
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
                                    if(!checkerUtil.checkLocationEnabled()) {
                                        Text(
                                            text = "Not Live",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
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
                                                    weatherUIState.weatherData?.locality,
                                                    weatherUIState.weatherData?.subLocality
                                                ), poppinsFont
                                            )
                                            WeatherDataCard(weatherUIState.weatherData!!, textColor, poppinsFont)
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = weatherUIState.forecastData != null,
                                    ) {
                                        LazyRow(
                                            modifier = Modifier
                                                .padding(horizontal = 5.dp)
                                        ) {
                                            items(weatherUIState.forecastData!!) { item ->
                                                ForecastItemComposable(item, poppinsFont)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        !weatherUIState.isCurrent -> {
                            QueryLocationWeatherComposable(weatherUIState.searchWeather)
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "No Weather data available, \nsearch new location"
                                )
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

fun showLocationToggleH(
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