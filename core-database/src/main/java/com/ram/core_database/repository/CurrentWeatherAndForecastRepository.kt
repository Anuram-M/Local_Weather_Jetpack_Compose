package com.ram.core_database.repository

import com.ram.core_database.entity.CurrentWeatherAndForecast
import kotlinx.coroutines.flow.Flow

interface CurrentWeatherAndForecastRepository {

    suspend fun insertData(currentWeather: CurrentWeatherAndForecast)

    fun fetchData(): Flow<CurrentWeatherAndForecast?>
}