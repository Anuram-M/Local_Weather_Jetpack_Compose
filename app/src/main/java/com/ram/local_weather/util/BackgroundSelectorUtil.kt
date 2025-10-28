package com.ram.local_weather.util

import androidx.compose.ui.graphics.Color

class BackgroundSelectorUtil {

    fun backgroundChoice(category: Int): Color {
        var position = 2
        val colors = listOf(
            Color(0XFF3A3A3A),
            Color(0xff89A7B1),
            Color(0xff4A90E2),
            Color(0xffE0F7FA),
            Color(0xffBDC3C7),
            Color(0xff56CCF2),
            Color(0xff757F9A)
        )
        position = category / 100
        if (category == 800) {
            position = 9
        }
        return when (position) {
            2 -> colors[0]
            3 -> colors[1]
            5 -> colors[2]
            6 -> colors[3]
            7 -> colors[4]
            8 -> colors[5]
            9 -> colors[6]
            else -> colors[2]
        }
    }
}