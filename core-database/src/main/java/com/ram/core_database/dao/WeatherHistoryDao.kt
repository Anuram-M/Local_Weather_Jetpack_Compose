package com.ram.core_database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ram.core_database.entity.WeatherHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(weatherHistory: WeatherHistory)

    @Query("select * from weather_history order by lastChecked desc")
    fun getHistory(): Flow<List<WeatherHistory>>

    @Query("select * from weather_history order by lastChecked desc")
    fun getHistoryPage(): PagingSource<Int, WeatherHistory>

    @Query("delete from weather_history where lastChecked <:keepTime")
    suspend fun deleteOldRecords(keepTime: Long)
}