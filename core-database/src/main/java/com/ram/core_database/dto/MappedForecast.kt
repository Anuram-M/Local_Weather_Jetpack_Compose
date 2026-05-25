package com.ram.core_database.dto

data class MappedForecast(
    val dateInMillis: Long,
    val icon: String,
    val temp: Double = 0.0
)
