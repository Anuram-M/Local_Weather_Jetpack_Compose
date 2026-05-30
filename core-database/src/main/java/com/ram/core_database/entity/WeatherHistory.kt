package com.ram.core_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_history")
data class WeatherHistory(
    @PrimaryKey
    val place: String,
    var temp: Double,
    var lastChecked: Long,
)
