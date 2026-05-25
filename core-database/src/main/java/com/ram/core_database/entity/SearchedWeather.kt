package com.ram.core_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searched_locations")
data class SearchedWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val cityName: String,
    val temp: Double,
    val minTemp: Double,
    val maxTemp: Double
)
