package com.ram.core_domain.repository

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.models.WeatherResponse

interface WeatherRepository{
    suspend fun getWeatherData( lat: Double,  lon: Double)  : NETWORK_RESULT<WeatherResponse>

    suspend fun getForeCaseData(lat: Double, lon: Double): NETWORK_RESULT<ForeCastResponse>

    suspend fun getWeatherDataFromLocation(location: String): NETWORK_RESULT<WeatherResponse>
}