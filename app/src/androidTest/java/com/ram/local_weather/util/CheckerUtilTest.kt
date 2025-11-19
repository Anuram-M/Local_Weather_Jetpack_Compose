package com.ram.local_weather.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CheckerUtilTest {
    lateinit var checkerUtil: CheckerUtil
    lateinit var context: Context
    @Before
    fun setUp() {
        checkerUtil = CheckerUtil()
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun checkLocationPermission() {
        val result = checkerUtil.checkLocationPermission(context)
        Assert.assertEquals(false, result)
    }

    @Test
    fun checkLocationEnabled() {
        val result = checkerUtil.checkLocationEnabled(context)
        Assert.assertEquals(false, result)
    }

    @Test
    fun checkNotificationPermission() {
        val result = checkerUtil.checkNotificationPermission(context)
        Assert.assertEquals(false, result)
    }
}