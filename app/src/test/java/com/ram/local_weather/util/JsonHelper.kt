package com.ram.local_weather.util

import java.io.InputStreamReader

object JsonHelper {

    fun getJson(fileName: String) : String {
        val inputStream = JsonHelper::class.java.getResourceAsStream(fileName)?: throw IllegalArgumentException("File Not FFound")
        val sb = StringBuilder()
        val reader = InputStreamReader(inputStream, Charsets.UTF_8)
        reader.readLines().forEach {
            sb.append(it)
        }
        return  sb.toString()
    }

}