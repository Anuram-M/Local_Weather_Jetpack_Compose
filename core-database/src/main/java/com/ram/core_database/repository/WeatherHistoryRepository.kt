package com.ram.core_database.repository

import com.ram.core_database.entity.WeatherHistory
import kotlinx.coroutines.flow.Flow

interface WeatherHistoryRepository {
    suspend fun insertWeather(weatherHistory: WeatherHistory)
    fun fetchHistory(): Flow<List<WeatherHistory>>
}