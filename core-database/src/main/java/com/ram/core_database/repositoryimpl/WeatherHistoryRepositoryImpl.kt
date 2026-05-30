package com.ram.core_database.repositoryimpl

import com.ram.core_database.MyDatabase
import com.ram.core_database.entity.WeatherHistory
import com.ram.core_database.repository.WeatherHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherHistoryRepositoryImpl @Inject constructor(): WeatherHistoryRepository {

    @Inject
    lateinit var db: MyDatabase

    override fun insertWeather(weatherHistory: WeatherHistory) {
        db.weatherHistoryDao().insertHistory(weatherHistory)
    }

    override fun fetchHistory(): Flow<List<WeatherHistory>> = flow {
        emit(db.weatherHistoryDao().getHistory())
    }.flowOn(Dispatchers.IO)
}