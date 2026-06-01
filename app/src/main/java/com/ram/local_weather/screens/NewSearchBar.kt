package com.ram.local_weather.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
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
    SearchBar(
        colors = SearchBarDefaults.colors(
            containerColor = Color(0xFFE0E0E0),
            dividerColor = Color.Black,
        ),
        expanded = expanded,
        onExpandedChange = { onExpandChange() },
        modifier = Modifier
            .heightIn(max = 350.dp)
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
            filteredList.isNotEmpty().let {
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
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
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