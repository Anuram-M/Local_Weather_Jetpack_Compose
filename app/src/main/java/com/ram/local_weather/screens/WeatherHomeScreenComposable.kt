package com.ram.local_weather.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ram.local_weather.UILOGIC_STATE
import com.ram.local_weather.viewmodels.LocationViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherHomeScreen(locationViewModel : LocationViewModel = hiltViewModel()) {

    val state by locationViewModel.uiLogicState.collectAsState()
    val context = LocalContext.current.applicationContext
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "permission"
    ) {
        composable("permission") { PermissionScreenComposable(locationViewModel) }
        composable("location") { LocationToggleComposable(context, locationViewModel) }
        composable("weather") { WeatherDataScreenComposable(locationViewModel) }
    }

    LaunchedEffect(state) {
        when(state){
            UILOGIC_STATE.LOGIC_PERMISSION_NEEDED -> {
                locationViewModel.updatePermission(false)
                if (navController.currentDestination?.route != "permission") {
                    navController.navigate("permission") {
                        popUpTo(0)
                    }
                }
            }
            UILOGIC_STATE.LOGIC_LOCATION_NEEDED -> {
                locationViewModel.updatePermission(true)
                if (navController.currentDestination?.route != "location") {
                    navController.navigate("location") {
                        popUpTo(0)
                    }
                }
            }
            UILOGIC_STATE.LOGIC_APP_READY -> {
                locationViewModel.updatePermission(true)
                if (navController.currentDestination?.route != "weather") {
                    navController.navigate("weather") {
                        popUpTo(0)
                    }
                }
            }
        }
    }
}