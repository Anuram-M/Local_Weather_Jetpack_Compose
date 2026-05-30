package com.ram.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.ram.core_database.entity.CurrentWeatherAndForecast

@Dao
interface WeatherAndForecastDao {

    @Insert(onConflict = REPLACE)
    fun insertWeatherAndForecast(weatherData: CurrentWeatherAndForecast)

    @Query("select * from current_weather")
    fun fetchCurrentData(): CurrentWeatherAndForecast?

}