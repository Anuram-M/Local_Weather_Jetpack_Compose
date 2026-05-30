package com.ram.core_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ram.core_database.dto.MappedForecast
import com.ram.core_database.dto.MappedWeather

@Entity(tableName = "current_weather")
data class CurrentWeatherAndForecast(
   @PrimaryKey
   val id: Int,
   val weather: MappedWeather,
   val forecast: List<MappedForecast>
)
