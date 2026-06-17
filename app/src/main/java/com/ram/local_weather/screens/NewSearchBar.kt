package com.ram.local_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ram.local_weather.R
import com.ram.local_weather.viewmodels.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandChange: () -> Unit,
    locationViewModel: LocationViewModel,
    onClear: () -> Unit,
    poppinsFont: FontFamily,
    navController: NavHostController
) {

    val searchList by locationViewModel.searchLocations.collectAsStateWithLifecycle()

    val config = LocalConfiguration.current

    val screenHeight = remember {
        mutableStateOf(config.screenHeightDp.dp)
    }

    val screenWidth = remember {
        mutableStateOf(config.screenWidthDp.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .safeContentPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val isLandscape = screenHeight.value < 480.dp
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        colors = SearchBarDefaults.colors(
                            containerColor = Color(0xFFE0E0E0),
                            dividerColor = Color.Transparent,
                        ),
                        expanded = expanded,
                        onExpandedChange = { onExpandChange() },
                        modifier = Modifier
                            .widthIn(max = (screenWidth.value / 2 - 80.dp))
                            .heightIn(max = 84.dp),
                        inputField = {
                            SearchBarDefaults.InputField(
                                colors = TextFieldDefaults.colors(
                                    unfocusedTextColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    cursorColor = Color.Black,
                                ),
                                query = query,
                                onQueryChange = { onQueryChange(it) },
                                onSearch = {
                                    onExpandChange()
                                    locationViewModel.getWeatherDataWithLocation(query.trim())
                                },
                                expanded = expanded,
                                onExpandedChange = { onExpandChange() },
                                placeholder = {
                                    Text(
                                        "Search by city...",
                                        color = Color.Gray,
                                        fontFamily = poppinsFont,
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.Black
                                    )
                                },
                                trailingIcon = {
                                    if (!expanded) {
                                    } else if (query.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                onQueryChange("")
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
                    ) {}
                    if (expanded) {
                        Box(
                            modifier = Modifier
                                .height(84.dp)
                                .weight(1f)
                                .widthIn(max = 500.dp)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            val filteredList = searchList.filter {
                                it.contains(query, ignoreCase = true)
                            }
                            if (!filteredList.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .width(500.dp)
                                        .height(84.dp)
                                        .padding(horizontal = 10.dp, vertical = 5.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        "Suggestions",
                                        modifier = Modifier.padding(10.dp),
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontFamily = poppinsFont,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    LazyRow(
                                        modifier = Modifier
                                            .widthIn(max = 500.dp)
                                            .padding(start = 10.dp)
                                            .fillMaxHeight()
                                    ) {
                                        items(filteredList) { item ->
                                            Row(
                                                modifier = Modifier
                                                    .clickable {
                                                        onQueryChange(item)
                                                        locationViewModel.getWeatherDataWithLocation(
                                                            item
                                                        )
                                                        onExpandChange()
                                                    }
                                                    .padding(end = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ping),
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    item,
                                                    fontFamily = poppinsFont,
                                                    style = TextStyle(color = Color.Black)
                                                )
                                            }
                                        }
                                    }
                                }

                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(), contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No Suggestions available!!",
                                        fontFamily = poppinsFont,
                                        style = TextStyle(color = Color.Black)
                                    )
                                }
                            }
                        }
                    }

                }
            } else {
                SearchBar(
                    colors = SearchBarDefaults.colors(
                        containerColor = Color(0xFFE0E0E0),
                        dividerColor = Color.Black,
                    ),
                    expanded = expanded,
                    onExpandedChange = { onExpandChange() },
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .heightIn(max = 320.dp)
                        .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            colors = TextFieldDefaults.colors(
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black,
                                cursorColor = Color.Black
                            ),
                            query = query,
                            onQueryChange = { onQueryChange(it) },
                            onSearch = {
                                onExpandChange()
                                locationViewModel.getWeatherDataWithLocation(query.trim())
                            },
                            expanded = expanded,
                            onExpandedChange = { onExpandChange() },
                            placeholder = {
                                Text(
                                    "Search by city...",
                                    color = Color.Gray,
                                    fontFamily = poppinsFont,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color.Black
                                )
                            },
                            trailingIcon = {
                                if (!expanded) {
//                        IconButton(
//                            onClick = {
//                                navController.navigate("history")
//                            }) {
//                            Icon(
//                                Icons.Default.ShoppingCart,
//                                contentDescription = "History",
//                                tint = Color.Black
//                            )
//                        }
                                } else if (query.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            onQueryChange("")
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
                    val filteredList = searchList.filter {
                        it.contains(query, ignoreCase = true)
                    }
                    if (filteredList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFDFDFDF))
                                .padding(5.dp)
                        ) {
                            items(filteredList) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onQueryChange(item)
                                            locationViewModel.getWeatherDataWithLocation(item)
                                            onExpandChange()
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ping),
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        item,
                                        fontFamily = poppinsFont,
                                        style = TextStyle(color = Color.Black)
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No Suggestions available!!",
                                fontFamily = poppinsFont,
                                style = TextStyle(color = Color.Black)
                            )
                        }
                    }
                }
            }
        }

    }


}