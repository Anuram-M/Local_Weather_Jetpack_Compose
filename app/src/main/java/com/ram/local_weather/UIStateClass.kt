package com.ram.local_weather

import com.ram.core_database.dto.MappedForecast
import com.ram.core_database.dto.MappedWeather

data class UIStateClass(
    var isLoading: Boolean = true,
    var isCurrent: Boolean = true,
    var weatherData: MappedWeather? = null,
    var forecastData: List<MappedForecast>? = null,
    var searchWeather: MappedWeather? = null,
    var error: String? = null
)