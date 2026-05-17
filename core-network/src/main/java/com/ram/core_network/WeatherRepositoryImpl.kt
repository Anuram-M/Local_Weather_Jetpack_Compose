package com.ram.core_network

import android.util.Log
import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.repository.WeatherRepository
import jakarta.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val weatherService: WeatherService) :
    WeatherRepository {

    override suspend fun getWeatherData(
        lat: Double,
        lon: Double
    ): NETWORK_RESULT<WeatherResponse> {
        val result = weatherService.getWeatherData(lat, lon)
        Log.d("WAPI", "getWeatherData: ${result}")
        return if (result.isSuccessful) {
            NETWORK_RESULT.Success(result.body())
        } else {
            NETWORK_RESULT.Error(null, result.message())
        }
    }

    override suspend fun getForeCaseData(
        lat: Double,
        lon: Double
    ): NETWORK_RESULT<ForeCastResponse> {
        val result = weatherService.getForeCastData(lat, lon)
        return if (result.isSuccessful) {
            NETWORK_RESULT.Success(result.body())
        } else {
            NETWORK_RESULT.Error(null, result.message())
        }
    }

    override suspend fun getWeatherDataFromLocation(location: String): NETWORK_RESULT<WeatherResponse> {
        val result = weatherService.getWeatherDataFromLocation(location)
        return if (result.isSuccessful) {
            NETWORK_RESULT.Success(result.body())
        } else {
            NETWORK_RESULT.Error(null, result.message())
        }
    }


}