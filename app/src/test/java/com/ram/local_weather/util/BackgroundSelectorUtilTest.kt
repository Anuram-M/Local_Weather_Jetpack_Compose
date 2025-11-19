package com.ram.local_weather.util

import androidx.compose.ui.graphics.Color
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BackgroundSelectorUtilTest {
    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun checkIfBackgroundIsCorrect() {
        val backgroundSelectorUtil = BackgroundSelectorUtil()
        val result = backgroundSelectorUtil.backgroundChoice(201)
        Assert.assertEquals(Color(0XFF3A3A3A), result)
    }
}