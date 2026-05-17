package com.ram.core_database.dto

import android.location.Address
import com.ram.core_domain.models.WeatherResponse

data class GPSandWeatherModel(
    val address: Address,
    val weatherResponse: WeatherResponse
)
