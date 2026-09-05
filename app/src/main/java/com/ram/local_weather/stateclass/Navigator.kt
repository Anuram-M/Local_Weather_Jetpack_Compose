package com.ram.local_weather.stateclass

sealed class Navigator {

    data class NavigateTo(val route: String) : Navigator()
    data object NavigateUp: Navigator()
}