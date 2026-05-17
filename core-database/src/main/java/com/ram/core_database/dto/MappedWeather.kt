package com.ram.core_database.dto

data class MappedWeather(
    val locality: String? = null,
    val subLocality: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val weather: String? = null,
    val description: String? = null,
    val weatherCategory: Int = 5,
    val icon: String? = null,
    val mainTemp: Double = 0.0,
    val minTemp: Double = 0.0,
    val maxTemp: Double = 0.0,
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
)
