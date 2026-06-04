package com.ram.local_weather.util

import android.content.Context
import android.os.Build
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
        context = ApplicationProvider.getApplicationContext()
        checkerUtil = CheckerUtil(context)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun checkLocationPermission() {
        val result = checkerUtil.checkLocationPermission()
        Assert.assertEquals(false, result)
    }

    @Test
    fun checkLocationEnabled() {
        val result = checkerUtil.checkLocationEnabled()
        Assert.assertEquals(true, result)
    }

    @Test
    fun checkNotificationPermission() {
        val result = checkerUtil.checkNotificationPermission()
        if (Build.VERSION.SDK_INT >= 33) {
            Assert.assertEquals(false, result)
        } else {
            Assert.assertTrue(result)
        }
    }
}