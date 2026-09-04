package com.ram.local_weather.util

import com.ram.local_weather.stateclass.Navigator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppNavigator {

   private val navigation = MutableSharedFlow<Navigator>(extraBufferCapacity = 1)
   val navDestination = navigation.asSharedFlow()

    fun navigateTo(route: String) {
        navigation.tryEmit(Navigator.NavigateTo(route))
    }

    fun navigateUp() {
        navigation.tryEmit(Navigator.NavigateUp)
    }
}