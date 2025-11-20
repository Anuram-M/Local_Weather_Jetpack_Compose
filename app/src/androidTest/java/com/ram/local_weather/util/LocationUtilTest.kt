package com.ram.local_weather.util

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LocationUtilTest {
    lateinit var locationUtil: LocationUtil

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mockRule = MockitoJUnit.rule()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Before
    fun setUp() {
        hiltRule.inject()
        locationUtil = LocationUtil(fusedLocationProviderClient)
    }

    @Test
    fun getLocationDetails() = runTest {

        val fakeLocation = mock<Location> {
            on { latitude } doReturn 12.0
            on { longitude } doReturn 77.6
        }

        val callCaptor = argumentCaptor<LocationCallback>()
        doAnswer {
            val locationResult = LocationResult.create(listOf(fakeLocation))
            callCaptor.firstValue.onLocationResult(locationResult)
            null
        }.`when`(fusedLocationProviderClient).requestLocationUpdates(
            any(),
            callCaptor.capture(),
            any()
        )

        val result = locationUtil.getLocationDetails()
        Assert.assertNotNull(result)
        verify(fusedLocationProviderClient).removeLocationUpdates(any<LocationCallback>())
    }
}