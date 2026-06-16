package com.ram.core_database.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.ram.core_database.entity.WeatherHistory
import kotlinx.coroutines.flow.Flow

interface WeatherHistoryRepository {
    suspend fun insertWeather(weatherHistory: WeatherHistory)
    fun fetchHistory(): Flow<List<WeatherHistory>>
    fun fetchHistoryP(size: Int): Flow<PagingData<WeatherHistory>>
}