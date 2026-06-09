package com.ram.local_weather.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import com.ram.local_weather.viewmodels.LocationViewModel


@Composable
fun SettingsComposable(locationViewModel: LocationViewModel, navController: NavHostController) {

    SettingsComposableContent(
        onNavigate = { route ->
            if(route.equals("back")) {
                navController.popBackStack()
            } else {
                navController.navigate(route)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsComposableContent(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                ),
                title = { Text("Settings") },
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
        Box(modifier = Modifier.fillMaxSize().background(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xffBDC3C7), Color.Black),
                start = Offset(0f, 0f),
                end = Offset(0f, Float.POSITIVE_INFINITY)
            )
        )) {
            Box(
                modifier = Modifier.fillMaxSize().safeContentPadding().padding(top = 60.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 20.dp)
                ) {
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onNavigate("history")
                        }
                    ) {
                        Text( text = "History",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                            .background(color = Color.Gray)
                    )

                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onNavigate("notification")
                        }
                    ) {
                        Text( text = "Notification",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                            .background(color = Color.Gray)
                    )
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onNavigate("releasenotes")
                        }
                    ) {
                        Text( text = "Release Notes",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                            .background(color = Color.Gray)
                    )
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onNavigate("about")
                        }
                    ) {
                        Text( text = "About",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(1.dp)
                            .background(color = Color.Gray)
                    )
                }
            }
        }

    }
}

@Preview
@Composable
fun previewSettings() {
    LocalWeatherTheme() {
        SettingsComposableContent {

        }
    }
}