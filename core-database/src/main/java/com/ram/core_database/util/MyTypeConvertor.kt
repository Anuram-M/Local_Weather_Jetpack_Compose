package com.ram.core_database.util

import android.text.TextWatcher
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ram.core_database.dto.MappedForecast
import com.ram.core_database.dto.MappedWeather

class MyTypeConvertor {
    val gson = Gson()

    @TypeConverter
    fun  fromT(data: MappedWeather) : String {
       return gson.toJson(data)
    }

    @TypeConverter
    fun  toT(data: String) : MappedWeather {
        return gson.fromJson(data, MappedWeather::class.java)
    }
}

class ForecastConvertor {

    companion object {
        private val gson = Gson()
    }

    @TypeConverter
    fun fromForecast(data: List<MappedForecast>?): String {
        return gson.toJson(data)
    }

    @TypeConverter
    fun toForecast(data: String?): List<MappedForecast> {
        if(data.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<MappedForecast>>(){}.type
        return gson.fromJson(data, type)
    }
}