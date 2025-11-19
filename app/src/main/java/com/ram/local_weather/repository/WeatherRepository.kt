package com.ram.local_weather.repository

import android.util.Log
import com.ram.local_weather.NETWORK_RESULT
import com.ram.local_weather.models.ForeCastResponse
import com.ram.local_weather.models.WeatherResponse
import com.ram.local_weather.networkservices.WeatherService
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherService: WeatherService) {

    suspend fun getWeatherData(lat: Double, long: Double) : NETWORK_RESULT<WeatherResponse> {
        val result = weatherService.getWeatherData(lat, long)
        Log.d("RESPOT", "getWeatherData: response ${result.isSuccessful}")
        return if(result.isSuccessful) {
            Log.d("RESPOT", "getWeatherData: ${result.body()}")
            NETWORK_RESULT.Success(result.body())
        } else {
            NETWORK_RESULT.Error(null,result.message())
        }
    }

    suspend fun getForeCaseData(lat: Double, long: Double) : NETWORK_RESULT<ForeCastResponse> {
        val result = weatherService.getForeCastData(lat, long)
        return if(result.isSuccessful) {
           NETWORK_RESULT.Success(result.body())
        } else {
            NETWORK_RESULT.Error(null, result.message())
        }
    }

    suspend fun getWeatherDataFromLocation(location: String) : NETWORK_RESULT<WeatherResponse> {
        val result = weatherService.getWeatherDataFromLocation(location)
        Log.d("RESTP", "getWeatherDataFromLocation: ${result.isSuccessful}")
        return if(result.isSuccessful) {
            NETWORK_RESULT.Success(result.body())
        } else {
            NETWORK_RESULT.Error(null, result.message())
        }
    }

}