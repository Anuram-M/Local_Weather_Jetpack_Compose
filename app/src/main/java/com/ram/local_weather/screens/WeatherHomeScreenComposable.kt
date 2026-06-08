package com.ram.local_weather.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.ram.local_weather.stateclass.NavStateClass
import com.ram.local_weather.viewmodels.LocationViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WeatherHomeScreen(
    navController: NavHostController,
    locationViewModel: LocationViewModel = hiltViewModel()
) {

    val context = LocalContext.current.applicationContext

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    NavHost(
        navController = navController,
        startDestination = "loading",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, // Start completely off-screen to the right
                animationSpec = tween(durationMillis = 400)
            )
        },
        // 2. Moving Forward: The current screen exiting slides left out of view
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth }, // Slide completely off-screen to the left
                animationSpec = tween(durationMillis = 400)
            )
        },
        // 3. Moving Backward (Back Press): The previous screen slides back in from the left
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(durationMillis = 400)
            )
        },
        // 4. Moving Backward (Back Press): The current screen slides out to the right
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 400)
            )
        }
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
        composable("restricted") { AgeRestrictedComposable(locationViewModel) }
    }

    LaunchedEffect(Unit) {
        locationViewModel.navEvents
            .flowWithLifecycle(
                lifecycleOwner.lifecycle,
                Lifecycle.State.STARTED
            ).collect { event ->
                when (event) {
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
                            navController.navigate("weather") {
                                popUpTo(0)
                            }
                        }
                    }

                    NavStateClass.NavigateToAppRestricted -> {
                        if(navController.currentDestination?.route != "restricted") {
                            navController.navigate("restricted") {
                                popUpTo(0)
                            }
                        }
                    }
                }
            }
    }
}