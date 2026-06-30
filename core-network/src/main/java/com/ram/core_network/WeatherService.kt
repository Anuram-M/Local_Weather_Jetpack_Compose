package com.ram.core_network

import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric"
    ) : Response<WeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForeCastData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units : String = "metric"
    ): Response<ForeCastResponse>

    @GET("data/2.5/weather")
    suspend fun getWeatherDataFromLocation(
        @Query("q") query: String,
        @Query("units") units: String = "metric"
    ) : Response<WeatherResponse>
}