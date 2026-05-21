package com.ram.core_database.repository

import com.ram.core_database.entiry.CurrentWeather
import kotlinx.coroutines.flow.Flow

interface CurrentWeatherRepository {

    fun insertWeather(currentWeather: CurrentWeather)

    fun fetchWeather(): Flow<CurrentWeather?>
}