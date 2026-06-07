package com.ram.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.ram.core_database.entity.CurrentWeatherAndForecast
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAndForecastDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertWeatherAndForecast(weatherData: CurrentWeatherAndForecast)

    @Query("select * from current_weather")
    fun fetchCurrentData(): Flow<CurrentWeatherAndForecast?>

}