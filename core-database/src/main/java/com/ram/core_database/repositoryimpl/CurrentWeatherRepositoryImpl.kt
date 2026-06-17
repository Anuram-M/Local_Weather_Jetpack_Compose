package com.ram.core_database.repositoryimpl

import com.ram.core_database.MyDatabase
import com.ram.core_database.entity.CurrentWeatherAndForecast
import com.ram.core_database.repository.CurrentWeatherAndForecastRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrentWeatherRepositoryImpl @Inject constructor() : CurrentWeatherAndForecastRepository {

    @Inject
    lateinit var db: MyDatabase

    override suspend fun insertData(currentWeather: CurrentWeatherAndForecast) {
       db.weatherDao().insertWeatherAndForecast(currentWeather)
    }

    override fun fetchData(): Flow<CurrentWeatherAndForecast?> {
        return db.weatherDao().fetchCurrentData()
    }
}