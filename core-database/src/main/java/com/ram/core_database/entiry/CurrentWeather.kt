package com.ram.core_database.entiry

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ram.core_database.dto.MappedWeather
import com.ram.core_domain.models.WeatherResponse

@Entity(tableName = "current_weather")
data class CurrentWeather(
   @PrimaryKey(autoGenerate = true)
   val id: Int,
   val isAvailable: Boolean,
   val data: MappedWeather

)
