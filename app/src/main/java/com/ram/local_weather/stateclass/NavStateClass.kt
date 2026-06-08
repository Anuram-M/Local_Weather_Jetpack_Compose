package com.ram.local_weather.stateclass

sealed class NavStateClass {
    object NavigateToPermission: NavStateClass()
    object NavigateToGPS: NavStateClass()
    object NavigateToHome: NavStateClass()
    object NavigateToAppRestricted: NavStateClass()
}