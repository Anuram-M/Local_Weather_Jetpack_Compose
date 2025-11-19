package com.ram.local_weather.util

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DateConvertorTest {
    lateinit var dateConvertor: DateConvertor

    //2025-11-05T12:36:17 = 1762326377
    @Before
    fun setUp() {
        dateConvertor = DateConvertor()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun convertEpoch() {
        val result = dateConvertor.convertEpoch(1762326377)
        Assert.assertEquals("2025-11-05T12:36:17", result.toString())
    }

    @Test
    fun getDayLabel() {
        val result = dateConvertor.getDayLabel(System.currentTimeMillis()/1000)
        Assert.assertEquals("Today", result)
    }

    @Test
    fun getDateAndTime() {
        val result = dateConvertor.getDateAndTime(1762326377)
        Assert.assertEquals("05-11-2025", result.first)
        Assert.assertEquals("12-36 PM", result.second)
    }

    @Test
    fun getMonthAndDate() {
        val result =  dateConvertor.getMonthAndDate(1762326377)
        Assert.assertEquals("Nov 05", result)
    }
}