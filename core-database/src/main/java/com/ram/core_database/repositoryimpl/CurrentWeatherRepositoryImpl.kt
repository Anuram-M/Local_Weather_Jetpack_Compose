package com.ram.core_database.repositoryimpl

import com.ram.core_database.MyDatabase
import com.ram.core_database.entity.CurrentWeather
import com.ram.core_database.repository.CurrentWeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CurrentWeatherRepositoryImpl @Inject constructor() : CurrentWeatherRepository {

    @Inject
    lateinit var db: MyDatabase

    override fun insertWeather(currentWeather: CurrentWeather) {
       db.weatherDao().insertWeather(currentWeather)
    }

    override fun fetchWeather(): Flow<CurrentWeather?> = flow {
        emit(db.weatherDao().fetchCurrentWeather())
    }.flowOn(Dispatchers.IO)
}