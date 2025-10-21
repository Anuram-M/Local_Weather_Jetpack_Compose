package com.ram.local_weather.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Address
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.ram.local_weather.ForecastItemComposable
import com.ram.local_weather.models.WeatherResponse
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.ui.theme.sarpanchFont
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
    val context = LocalContext.current.applicationContext
    var isRefreshing by remember { mutableStateOf(false) }

    val refreshState = rememberPullToRefreshState()

    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        isLoading = true
        locationViewModel.getLocationUpdates()
    }


    val location by locationViewModel.location
    val address by locationViewModel.address
    val weatherData by locationViewModel.weatherData
    val forecastData by locationViewModel.forecastData

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
                    if (isEnabled != lastKnownState) {
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


    LaunchedEffect(weatherData) {
        if (weatherData != null) {
            isLoading = false
            isRefreshing = false
        }
    }
    when {
        location != null -> {
            val workRequest =
                PeriodicWorkRequestBuilder<WorkManagerUtil>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("UNiq", ExistingPeriodicWorkPolicy.KEEP, workRequest)
        }
    }

    PullToRefreshBox(
        modifier = Modifier
            .systemBarsPadding()
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

            weatherData != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .background(Color.White),
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
                        NewSearchBarExample()
                        Card(
                            modifier = Modifier
                                .padding(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE9E9E9)
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

                                WeatherDataCard(weatherData, textColor, sarpanchFont)

                            }

                        }

                        AnimatedVisibility(
                            visible = forecastData != null,
                        ) {
                            LazyRow(
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                                    .weight(1f)
                            ) {
                                items(forecastData?.list!!) { item ->
                                    ForecastItemComposable(item, sarpanchFont)
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
                    it,
                    fontSize = 24.sp,
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
                    fontWeight = FontWeight.Bold,
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFDFDFDF),
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
                    modifier = Modifier
                        .background(
                            Color(0xFFDFDFDF),
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
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )

    var typeText by remember {
        mutableStateOf("")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Column {
            OutlinedTextField(
                enabled = false,
                colors = TextFieldDefaults.colors(),
                value = typeText,
                onValueChange = { typeText = it },
                label = { Text("Label2") },   // label should be in a lambda
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(15.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .width(200.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .width(140.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .width(140.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .width(100.dp)
                                .height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .width(100.dp)
                                .height(50.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(15.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .width(200.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .width(140.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(brush)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .width(140.dp)
                            .height(20.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .width(100.dp)
                                .height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(brush)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .width(100.dp)
                                .height(50.dp)
                        )
                    }
                }
            }
        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSearchBarExample() {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        colors = SearchBarDefaults.colors(
            containerColor = Color(0xFFDFDFDF),
            dividerColor = Color.Black,
        ),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .background(Color.White)
            .padding(start = 5.dp, end = 5.dp, bottom = 5.dp),
        inputField = {
            SearchBarDefaults.InputField(
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                query = query,
                onQueryChange = { query = it },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text("Search items...", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Black
                            )
                        }
                    }
                }
            )
        },
        tonalElevation = 6.dp,
        shadowElevation = 10.dp
    ) {
        val sampleData = listOf("Thanjavur", "Chennai", "Coimbatore", "Bangalore", "Delhi")
        val filteredList = sampleData.filter {
            it.contains(query, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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
                            expanded = false
                        }
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item)
                }
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