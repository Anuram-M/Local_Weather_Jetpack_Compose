package com.ram.local_weather.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
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

    val density = LocalDensity.current
//    Log.d("SERBAR", "NewSearchBar: ${density}")

    val searchbarBottomPx by remember { mutableStateOf(0f) }

    val windowKeyboardPx = WindowInsets.ime
//    Log.d("SERBAR", "NewSearchBar: ime: ${windowKeyboardPx}, search ${searchbarBottomPx}")

    val screenHeightPx = with(density) { WindowInsets.systemBars.getBottom(this) + WindowInsets.ime.getBottom(this)}

//    Log.d("SERBAR", "NewSearchBar: screen height - keyboard : ${screenHeightPx}")

    val imeBottom = WindowInsets.ime.getBottom(density)
//    Log.d("SERBAR", "NewSearchBar: keyboard top : ${imeBottom}")

    val systemChannels = WindowInsets.systemBars.getBottom(density)
//    Log.d("SERBAR", "NewSearchBar: ${systemChannels}")

    val availableGap = remember(searchbarBottomPx, imeBottom) {
       val gapInPx =  if(imeBottom > 0) {
           (searchbarBottomPx)
       } else {
           Float.MAX_VALUE
       }
        gapInPx
    }

    Log.d("SERBAR", "NewSearchBar: available : ${availableGap}")

    var availableHeight by remember { mutableStateOf(500.dp) }

    val imeHeight = with(density) { WindowInsets.ime.getBottom(this).toDp() }

    SearchBar(
        colors = SearchBarDefaults.colors(
            containerColor = Color(0xFFE0E0E0),
            dividerColor = Color.Black,
        ),
        expanded = expanded,
        onExpandedChange = { onExpandChange() },
        modifier = Modifier
            .widthIn(max = 400.dp)
            .heightIn(max = 250.dp)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
            .onGloballyPositioned { coordinates ->
                availableHeight = with(density) { coordinates.size.height.toDp() }
            },
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

        Log.d("SERBAR", "NewSearchBar: aheight : ${availableHeight}")
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