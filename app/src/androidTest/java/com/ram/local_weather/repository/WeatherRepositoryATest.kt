package com.ram.local_weather.repository

import com.ram.local_weather.NETWORK_RESULT
import com.ram.local_weather.networkservices.WeatherService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import retrofit2.Response
import javax.inject.Inject

@HiltAndroidTest
class WeatherRepositoryATest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var weatherService: WeatherService

    lateinit var weatherRepository: WeatherRepository
    @Before
    fun setUp() {
        hiltRule.inject()
        weatherRepository = WeatherRepository(weatherService)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getWeatherDataError() = runTest {
        Mockito.`when`(weatherService.getWeatherData(ArgumentMatchers.anyDouble(), ArgumentMatchers.anyDouble(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(
            Response.error(404, "Something went wrong".toResponseBody()))
        val result = weatherRepository.getWeatherData(10.0, 20.0)
        Assert.assertEquals(true, result is NETWORK_RESULT.Error)
    }

}