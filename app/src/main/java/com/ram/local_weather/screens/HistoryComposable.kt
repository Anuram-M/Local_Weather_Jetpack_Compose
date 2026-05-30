package com.ram.local_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.viewmodels.LocationViewModel
import kotlin.math.round


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryComposable(locationViewModel: LocationViewModel, navController: NavController) {

    val history by locationViewModel.historyData.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        locationViewModel.fetchHistory()
    }
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                ),
                title = {
                    Text(text = "History")
                },
                navigationIcon = {
                    // ✅ This puts the back arrow exactly where it belongs
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            // AutoMirrored ensures the arrow flips correctly for RTL languages like Arabic
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().systemBarsPadding().padding(top = 60.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ) {
                items(history!!) {item ->
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(5.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xffBDC3C7)
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(15.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.place,
                                    style = TextStyle(
                                        fontFamily = poppinsFont,
                                        fontSize = 24.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Text(
                                        text = "${round( item.temp).toInt()}ºC",
                                        style = TextStyle(
                                            fontFamily = poppinsFont,
                                            fontSize = 20.sp,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        item.lastChecked.toString()
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }

}