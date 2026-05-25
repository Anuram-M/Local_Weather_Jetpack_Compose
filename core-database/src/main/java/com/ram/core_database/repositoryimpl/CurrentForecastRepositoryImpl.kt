package com.ram.core_database.repositoryimpl

import com.ram.core_database.MyDatabase
import com.ram.core_database.entity.CurrentForecast
import com.ram.core_database.repository.CurrentForecastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CurrentForecastRepositoryImpl @Inject constructor() : CurrentForecastRepository {

    @Inject
    lateinit var db: MyDatabase

    override fun insertForecast(currentForecast: CurrentForecast) {
        db.forecastDao().insertForecast(currentForecast)
    }

    override fun updateForecast(currentForecast: CurrentForecast) {
        db.forecastDao().updateForecast(currentForecast)
    }

    override fun queryForecast(): Flow<CurrentForecast?> = flow{
        emit(db.forecastDao().getForecast())
    }.flowOn(Dispatchers.IO)
}