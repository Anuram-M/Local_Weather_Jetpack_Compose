package com.ram.core_database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ram.core_database.dto.MappedForecast

@Entity(tableName = "current_forecast")
data class CurrentForecast(
    @PrimaryKey
    val id: Int,
    val data: List<MappedForecast>
)
