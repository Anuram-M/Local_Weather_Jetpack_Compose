package com.ram.local_weather.stateclass

import com.ram.core_database.entity.WeatherHistory

sealed class HistoryUIData {
    data class Header(val header: String): HistoryUIData()
    data class Item(val data: WeatherHistory): HistoryUIData()
}