package com.ram.local_weather.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ram.core_domain.models.WeatherResponse
import com.ram.local_weather.ForecastItemComposable
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.util.BackgroundSelectorUtil
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.util.WorkManagerUtil
import com.ram.local_weather.viewmodels.LocationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherDataScreenComposable(
    locationViewModel: LocationViewModel
) {
    Log.d("CHOR", "weather: starting")
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


    val location by locationViewModel.location
    val address by locationViewModel.address
    val weatherData by locationViewModel.weatherData
    val forecastData by locationViewModel.forecastData
    var searchWeatherData by locationViewModel.searchWeather
    val refreshCount by locationViewModel.refreshCount


    val mAddress = if (address != null) {
        "${address!!.subLocality}, ${address!!.locality}"
    } else {
        "address not found"
    }
    val textColor = Color.Black
    val canShowWeatherCard = weatherData != null

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
        targetValue = if(isDay) Color.Black else Color.Black,
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
            animationWidget = weatherData?.weather!![0].main
            isLoading = false
            isRefreshing = false
        }
    }

    LaunchedEffect(searchWeatherData) {
        if(searchWeatherData!=null) {
            isDay = searchWeatherData?.weather!![0].icon.endsWith('d')
            animationWidget = searchWeatherData?.weather!![0].main
            mainBg = BackgroundSelectorUtil().backgroundChoice(searchWeatherData?.weather!![0].id)
        }
        if(searchWeatherData == null && weatherData != null) {
            animationWidget = weatherData?.weather!![0].main
            isDay = weatherData?.weather!![0].icon.endsWith('d')
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherData?.weather!![0].id)
        }
    }

    LaunchedEffect(refreshCount) {
        weatherData?.let {
            if(searchWeatherData == null) {
                animationWidget = weatherData?.weather!![0].main
            }
            isDay = weatherData?.weather!![0].icon.endsWith('d')
            Log.d("POPOI", "WeatherDataScreenComposable: icon : ${weatherData?.weather!![0].icon}, is it day : ${weatherData?.weather!![0].icon.endsWith('d')}, $isDay")
            mainBg = BackgroundSelectorUtil().backgroundChoice(weatherData?.weather!![0].id)
        }
    }
    LaunchedEffect(Unit)  {
        WorkManager.getInstance(context).cancelAllWork()
            val workRequest =
                PeriodicWorkRequestBuilder<WorkManagerUtil>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("UNiq", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(animatedBackground, animatedDayAccent)
                )
            ))

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
        Column(modifier = Modifier.systemBarsPadding()) {
            NewSearchBarExample(
                locationViewModel,
                isSearchWeather,
                onSearch = { isSearchWeather = true },
                onClear = {
                    isLoading = true
                    locationViewModel.getLocationUpdates()
                    locationViewModel.resetSearchWeather() },
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
                        ShimmerPlaceholderList()
                    }

                    searchWeatherData != null -> {
                        SearchWeatherDataScreen(searchWeatherData!!)
                    }

                    weatherData != null -> {
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
                                    ), elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        LocalityCard(address, poppinsFont)

                                        WeatherDataCard(weatherData, textColor, poppinsFont)

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
fun LocalityCard(address: Address?, spFont: FontFamily) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = address?.subLocality != null
        ) {
            address?.subLocality?.let {
                Text(
                    address?.subLocality!!,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = spFont
                )
            }

        }
        AnimatedVisibility(
            visible = address?.locality != null
        ) {
            address?.locality?.let {
                Text(
                    it,
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontFamily = spFont
                )
            }

        }
    }
}

@Composable
fun WeatherDataCard(weatherData: WeatherResponse?, textColor: Color, spFont: FontFamily) {
    AnimatedVisibility(visible = weatherData != null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                AsyncImage(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    model = "https://openweathermap.org/img/wn/${weatherData!!.weather[0].icon}@4x.png",
                    contentDescription = "Weather Icon",
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        round(weatherData!!.main.temp).toInt().toString() + "ºC",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontFamily = spFont
                    )

                }

            }

            Text(
                weatherData!!.weather[0].description.replaceFirstChar { it.uppercase() },
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 5.dp)) {
                        Text(
                            text = "Low ↓",
                            fontSize = 14.sp,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontFamily = spFont
                        )
                        Text(
                            text = "%.1f".format(weatherData!!.main.temp_min) + "ºC",
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
                            text = "%.1f".format(weatherData!!.main.temp_min) + "ºC",
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
                        ).padding(vertical = 5.dp)
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
                            text = weatherData!!.main.humidity.toString() + "%",
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
                        ).padding(vertical = 5.dp)
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
                            text = "%.1f".format((weatherData?.wind?.speed ?: 0.0) * 3.6) + " km/h",
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
fun ShimmerPlaceholderList() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val brush = remember(translateAnim){
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 200f, translateAnim - 200f),
            end = Offset(translateAnim, translateAnim)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(15.dp), verticalArrangement = Arrangement.SpaceEvenly) {

            ShimmerCard(Modifier
                .fillMaxWidth()
                .weight(1f),
                brush)
            Spacer(Modifier.height(10.dp))
            ShimmerCard(Modifier
                .fillMaxWidth()
                .weight(1f),
                brush)


        }


    }

}

@Composable
fun ShimmerCard(modifier: Modifier, brush: Brush) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
//                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(200.dp)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
//                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(140.dp)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
//                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .size(100.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
//                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .width(140.dp)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
//                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .width(100.dp)
                        .height(50.dp)
                )
                Spacer(modifier = Modifier.width(30.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush)
//                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .width(100.dp)
                        .height(50.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSearchBarExample(
    locationViewModel: LocationViewModel,
    isSearchWeather: Boolean,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    searchList: List<String>,
    poppinsFont: FontFamily
) {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        colors = SearchBarDefaults.colors(
            containerColor = Color(0xFFE0E0E0),
            dividerColor = Color.Black,
        ),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 350.dp)
//            .background(Color.White)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        inputField = {
            SearchBarDefaults.InputField(
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    expanded = false
                    locationViewModel.getWeatherDataWithLocation(query.trim())
                           },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search weather by location...", color = Color.Gray, fontFamily = poppinsFont, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                                onClear()
                            }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Black
                            )
                        }
                    }
                },

            )
        },
        tonalElevation = 0.dp,
        shadowElevation = 4.dp
    ) {
//        val sampleData = listOf("Thanjavur", "Chennai", "Coimbatore", "Bangalore", "Delhi")
        val filteredList = searchList.filter {
            it.contains(query, ignoreCase = true)
        }

        if(filteredList.isNotEmpty()) {
            filteredList.isNotEmpty().let {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFDFDFDF))
                        .heightIn(max = 300.dp)
                        .padding(5.dp)
                ) {
                    items(filteredList) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    query = item

//                            isSearchWeather = true
                                    locationViewModel.getWeatherDataWithLocation(item)
                                    expanded = false
                                    onSearch
                                }
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item, fontFamily = poppinsFont)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No Suggestions available!!")
            }

        }

    }
}


@Preview
@Composable
fun previewShimmer() {
    LocalWeatherTheme {
        ShimmerPlaceholderList()
    }
}