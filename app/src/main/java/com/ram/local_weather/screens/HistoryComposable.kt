package com.ram.local_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ram.local_weather.R
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.util.DateConvertor
import com.ram.local_weather.viewmodels.LocationViewModel
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HistoryComposable(locationViewModel: LocationViewModel, navController: NavController) {

    val history by locationViewModel.historyData.collectAsStateWithLifecycle()

    val historyPaged = locationViewModel.pagedHistory.collectAsLazyPagingItems()


    val config = LocalConfiguration.current
    val screenDp = remember {
        config.screenWidthDp.dp
    }
    val groupOrder = rememberSaveable {
        mutableStateOf("Timeline")
    }
    val groupedList = remember {
        mutableStateOf(
            history
                ?.sortedByDescending {
                    it.lastChecked
                }
                ?.groupBy { item -> DateConvertor.getMonthGroup(item.lastChecked) }
        )
    }
    LaunchedEffect(history, groupOrder.value) {
        if (groupOrder.value.equals("Alphabetical")) {
            groupedList.value = history
                ?.sortedBy {
                    it.place
                }
                ?.groupBy { it.place[0].toString() }
        } else {
            groupedList.value = history
                ?.sortedByDescending {
                    it.lastChecked
                }
                ?.groupBy { item -> DateConvertor.getMonthGroup(item.lastChecked) }
        }
    }
    val dateConverter = remember {
        mutableStateOf(DateConvertor)
    }

    var expandMenu by remember {
        mutableStateOf(false)
    }

    Scaffold(
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
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xffBDC3C7), Color.Black),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .safeContentPadding()
                    .padding(top = 60.dp)

            ) {
                if (!history.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 10.dp, end = 10.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sort By",
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(
                                modifier = Modifier.size(20.dp)
                            )
                            Box() {
                                Row(
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = Color.Black,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .width(149.dp)
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                        .clickable {
                                            expandMenu = !expandMenu
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = groupOrder.value,
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .padding(),
                                        textAlign = TextAlign.Start,
                                        style = TextStyle(
                                            color = Color.Black
                                        )
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(R.drawable.on_location),
                                        tint = Color.Black,
                                        contentDescription = null
                                    )
                                }

                                DropdownMenu(
                                    expanded = expandMenu,
                                    onDismissRequest = { expandMenu = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Alphabetical") },
                                        onClick = {
                                            groupOrder.value = "Alphabetical"
                                            expandMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Timeline") },
                                        onClick = {
                                            groupOrder.value = "Timeline"
                                            expandMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {

                            //checking the paged list
                            items(
                                count = historyPaged.itemCount,
                                key = historyPaged.itemKey { it.lastChecked }
                            ) { currentItem ->
                                val item = historyPaged[currentItem]!!
                                val relativeTime =
                                    dateConverter.value.getExactMomentAgo(item.lastChecked)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                            bottom = 10.dp
                                        )
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xffBDC3C7)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(15.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                item.place,
                                                style = TextStyle(
                                                    fontFamily = poppinsFont,
                                                    fontSize = 20.sp,
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                            Column(
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.Center
                                            ) {

                                                Text(
                                                    text = "${round(item.temp).toInt()} ℃",
                                                    style = TextStyle(
                                                        fontFamily = poppinsFont,
                                                        fontSize = 20.sp,
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                                Text(
                                                    relativeTime,
                                                    style = TextStyle(
                                                        fontSize = 12.sp,
                                                        color = Color.DarkGray
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
//                            groupedList.value?.forEach { (tag, hList) ->
//
//                                item(key = tag) {
//                                    Card(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(vertical = 5.dp),
//                                        colors = CardDefaults.cardColors(
//                                            containerColor = Color.DarkGray.copy(alpha = 0.4f)
//                                        )
//                                    ) {
//                                        Column() {
//                                            Text(
//                                                modifier = Modifier
//                                                    .fillMaxWidth()
//                                                    .padding(10.dp),
//                                                text = tag,
//                                                style = TextStyle(
//                                                    fontFamily = poppinsFont,
//                                                    fontWeight = FontWeight.ExtraBold,
//                                                    color = Color.Black,
//                                                    fontSize = 20.sp
//                                                )
//                                            )
//                                            FlowRow(
//                                                modifier = Modifier.fillMaxWidth(),
//                                                maxItemsInEachRow = if (screenDp < 450.dp) 1 else 2
//                                            ) {
//                                                hList.forEach { item ->
//                                                    val relativeTime =
//                                                        dateConverter.value.getExactMomentAgo(item.lastChecked)
//                                                    Box(
//                                                        modifier = Modifier
//                                                            .weight(1f)
//                                                            .padding(
//                                                                start = 10.dp,
//                                                                end = 10.dp,
//                                                                bottom = 10.dp
//                                                            )
//                                                    ) {
//                                                        Card(
//                                                            modifier = Modifier.fillMaxWidth(),
//                                                            colors = CardDefaults.cardColors(
//                                                                containerColor = Color(0xffBDC3C7)
//                                                            )
//                                                        ) {
//                                                            Row(
//                                                                modifier = Modifier
//                                                                    .fillMaxWidth()
//                                                                    .padding(15.dp),
//                                                                horizontalArrangement = Arrangement.SpaceBetween,
//                                                                verticalAlignment = Alignment.CenterVertically
//                                                            ) {
//                                                                Text(
//                                                                    item.place,
//                                                                    style = TextStyle(
//                                                                        fontFamily = poppinsFont,
//                                                                        fontSize = 20.sp,
//                                                                        color = Color.Black,
//                                                                        fontWeight = FontWeight.SemiBold
//                                                                    )
//                                                                )
//                                                                Column(
//                                                                    horizontalAlignment = Alignment.End,
//                                                                    verticalArrangement = Arrangement.Center
//                                                                ) {
//
//                                                                    Text(
//                                                                        text = "${round(item.temp).toInt()} ℃",
//                                                                        style = TextStyle(
//                                                                            fontFamily = poppinsFont,
//                                                                            fontSize = 20.sp,
//                                                                            color = Color.Black,
//                                                                            fontWeight = FontWeight.Bold
//                                                                        )
//                                                                    )
//                                                                    Text(
//                                                                        relativeTime,
//                                                                        style = TextStyle(
//                                                                            fontSize = 12.sp,
//                                                                            color = Color.DarkGray
//                                                                        )
//                                                                    )
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "No records found!",
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }
                }
            }

        }
    }

}