package com.ram.core_database.dto

import com.ram.core_domain.models.WeatherResponse

data class GPSandWeatherModel(
    val subLocality: String? = null,
    val locality: String? = null,
    val weatherResponse: WeatherResponse
)
