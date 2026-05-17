package com.ram.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.ram.core_database.dto.MappedWeather
import com.ram.core_database.entiry.CurrentWeather

@Dao
interface WeatherDao {

    @Insert(onConflict = REPLACE)
    fun insertWeather(weatherData: CurrentWeather)

    @Query("select * from current_weather")
    fun fetchCurrentWeather(): CurrentWeather?

}