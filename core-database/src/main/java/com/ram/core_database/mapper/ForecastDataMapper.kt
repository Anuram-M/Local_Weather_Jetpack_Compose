package com.ram.core_database.mapper

import com.ram.core_database.dto.MappedForecast
import com.ram.core_domain.models.ForecastItem
import kotlin.math.round

fun ForecastItem.toMappedForecast(): MappedForecast {
    return MappedForecast(
        dateInMillis = this.dt,
        icon = this.weather[0].icon,
        temp = round(this.main.temp)
    )
}