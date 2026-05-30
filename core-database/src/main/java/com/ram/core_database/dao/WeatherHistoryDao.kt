package com.ram.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ram.core_database.entity.WeatherHistory

@Dao
interface WeatherHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(weatherHistory: WeatherHistory)

    @Query("select * from weather_history order by lastChecked desc")
    fun getHistory(): List<WeatherHistory>

}