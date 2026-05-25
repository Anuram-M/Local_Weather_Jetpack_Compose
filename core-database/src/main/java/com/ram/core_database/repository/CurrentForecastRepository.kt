package com.ram.core_database.repository

import com.ram.core_database.entity.CurrentForecast
import kotlinx.coroutines.flow.Flow

interface CurrentForecastRepository {

    fun insertForecast(currentForecast: CurrentForecast)

    fun updateForecast(currentForecast: CurrentForecast)

    fun queryForecast(): Flow<CurrentForecast?>
}