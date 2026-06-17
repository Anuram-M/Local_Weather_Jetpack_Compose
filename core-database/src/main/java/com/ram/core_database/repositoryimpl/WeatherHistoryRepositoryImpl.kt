package com.ram.core_database.repositoryimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ram.core_database.MyDatabase
import com.ram.core_database.entity.WeatherHistory
import com.ram.core_database.repository.WeatherHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherHistoryRepositoryImpl @Inject constructor(): WeatherHistoryRepository {

    @Inject
    lateinit var db: MyDatabase

    override suspend fun insertWeather(weatherHistory: WeatherHistory) {
        db.weatherHistoryDao().insertHistory(weatherHistory)
    }

    override fun fetchHistory(): Flow<List<WeatherHistory>> {
        return db.weatherHistoryDao().getHistory()
    }

    override fun fetchHistoryP(size: Int): Flow<PagingData<WeatherHistory>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                prefetchDistance = size / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { db.weatherHistoryDao().getHistoryPage() }
        ).flow
    }

    override fun fetchHistoryPByPlace(size: Int): Flow<PagingData<WeatherHistory>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                prefetchDistance = size / 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { db.weatherHistoryDao().getHistoryPageByPlace() }
        ).flow
    }
}