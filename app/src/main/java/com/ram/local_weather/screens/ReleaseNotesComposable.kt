package com.ram.local_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ram.core_domain.models.ReleaseData
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.ui.theme.poppinsFont
import com.ram.local_weather.viewmodels.LocationViewModel


@Composable
fun ReleaseNotesComposable(locationViewModel: LocationViewModel, navHostController: NavHostController) {
    val releaseNotes by locationViewModel.releaseNotes.collectAsStateWithLifecycle()

    ReleaseNotesComposableContent(releaseNotes, onNavigate = { route ->
        if(route.equals("back")) {
            navHostController.popBackStack()
        } else {
            navHostController.navigate(route)
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseNotesComposableContent(releaseNotes: List<ReleaseData>, onNavigate: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                ),
                title = { Text("Release Notes") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigate("back")
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
            modifier = Modifier.fillMaxSize().background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xffBDC3C7), Color.Black),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize().safeContentPadding().padding(top = 60.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 10.dp)
                ) {
                    items(releaseNotes) {item ->
                        val points = item.notes.split("*")
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Gray
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 5.dp
                            ),
                            modifier = Modifier.fillMaxWidth().padding( horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    text = "v${item.version}",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f).background(
                                        shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                                        , color = Color.Gray).padding(20.dp),
                                    style = TextStyle(
                                       color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = poppinsFont
                                    )
                                )
                                Column(
                                    modifier = Modifier.weight(3f).background(Color.LightGray).padding(20.dp)
                                ) {
                                    points.forEach {
                                        Text(
                                            text = "◉ ${it}",
                                            style = TextStyle(
                                                color = Color.Black,
                                                fontWeight = FontWeight.Normal,
                                                fontFamily = poppinsFont
                                            )
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
}

@Preview
@Composable
fun previewRelease() {
    val releaseNotes = listOf(ReleaseData(
        version = "1.0",
        notes = "Local weather though gps and search.*Forecast data"
    ))
    LocalWeatherTheme {
        ReleaseNotesComposableContent(releaseNotes) { }
    }
}