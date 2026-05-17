package com.ram.core_database.mapper

import com.ram.core_database.dto.GPSandWeatherModel
import com.ram.core_database.dto.MappedWeather
import kotlin.math.round

fun GPSandWeatherModel.toMappedWeather(): MappedWeather {
    return MappedWeather(
        locality = this.address.locality,
        subLocality = this.address.subLocality,
        latitude = this.weatherResponse.coord.lat,
        longitude = this.weatherResponse.coord.lon,
        weather = this.weatherResponse.weather[0].main,
        description = this.weatherResponse.weather[0].description.replaceFirstChar { it.uppercase() },
        weatherCategory = this.weatherResponse.weather[0].id,
        icon = this.weatherResponse.weather[0].icon,
        mainTemp = round(this.weatherResponse.main.temp),
        minTemp = "%.1f".format(this.weatherResponse.main.temp_min).toDouble(),
        maxTemp = "%.1f".format(this.weatherResponse.main.temp_max).toDouble(),
        humidity = this.weatherResponse.main.humidity,
        windSpeed = "%.1f".format((this.weatherResponse.wind.speed ?: 0.0) * 3.6).toDouble()
    )
}