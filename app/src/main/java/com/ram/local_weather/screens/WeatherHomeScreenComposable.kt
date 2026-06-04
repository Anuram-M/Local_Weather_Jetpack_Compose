package com.ram.local_weather.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ram.local_weather.UILOGIC_STATE
import com.ram.local_weather.stateclass.NavStateClass
import com.ram.local_weather.viewmodels.LocationViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherHomeScreen(navController: NavHostController, locationViewModel: LocationViewModel = hiltViewModel()) {

    val state by locationViewModel.uiLogicState.collectAsState()
    val context = LocalContext.current.applicationContext
//    val navController = rememberNavController()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
//    Log.d("SATSAT", "WeatherHomeScreen: ${state}")
    NavHost(
        navController = navController,
        startDestination = "loading",
    ) {
        composable("loading") { LoadingScreen() }
        composable("permission") { PermissionScreenComposable(locationViewModel, navController) }
        composable("location") {
            LocationToggleComposable(
                context,
                locationViewModel,
                navController
            )
        }
        composable("weather") { WeatherHomeComposable(locationViewModel, navController) }
        composable("history") { HistoryComposable(locationViewModel, navController) }
    }

    LaunchedEffect(Unit) {
        locationViewModel.navEvents
            .flowWithLifecycle(
                lifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).collect { event ->
               when(event) {
                   is NavStateClass.NavigateToPermission -> {
                       locationViewModel.updatePermission(false)
                       if (navController.currentDestination?.route != "permission") {
                           navController.navigate("permission") {
                               popUpTo(0)
                           }
                       }
                   }

                   NavStateClass.NavigateToGPS -> {
                       locationViewModel.updatePermission(true)
                       if (navController.currentDestination?.route != "location") {
                           navController.navigate("location") {
                               popUpTo(0)
                           }
                       }
                   }
                   NavStateClass.NavigateToHome -> {
                       locationViewModel.updatePermission(true)
                       if (navController.currentDestination?.route != "weather") {
                           Log.d("SATSAT", "WeatherHomeScreen: trigger")
                           navController.navigate("weather") {
                               popUpTo(0)
                           }
                       }
                   }
               }
            }
    }
//    LaunchedEffect(state) {
//        when (state) {
//            UILOGIC_STATE.LOGIC_PERMISSION_NEEDED -> {
//                locationViewModel.updatePermission(false)
//                if (navController.currentDestination?.route != "permission") {
//                    navController.navigate("permission") {
//                        popUpTo(0)
//                    }
//                }
//            }
//
//            UILOGIC_STATE.LOGIC_LOCATION_NEEDED -> {
//                locationViewModel.updatePermission(true)
//                if (navController.currentDestination?.route != "location") {
//                    navController.navigate("location") {
//                        popUpTo(0)
//                    }
//                }
//            }
//
//            UILOGIC_STATE.LOGIC_APP_READY -> {
//                locationViewModel.updatePermission(true)
//                if (navController.currentDestination?.route != "weather") {
//                    Log.d("SATSAT", "WeatherHomeScreen: trigger")
//                    navController.navigate("weather") {
//                        popUpTo(0)
//                    }
//                }
//            }
//
//            UILOGIC_STATE.LOGIC_LOADING -> {
//                if (navController.currentDestination?.route != "loading") {
//                    navController.navigate("loading") {
//                        popUpTo(0)
//                    }
//                }
//            }
//        }
//    }
}