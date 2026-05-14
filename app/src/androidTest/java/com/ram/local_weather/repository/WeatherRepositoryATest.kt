package com.ram.local_weather.repository

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_network.WeatherRepositoryImpl
import com.ram.core_network.WeatherService
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
import javax.inject.Inject
import retrofit2.Response

@HiltAndroidTest
class WeatherRepositoryATest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var weatherService: WeatherService

    lateinit var weatherRepository: WeatherRepositoryImpl
    @Before
    fun setUp() {
        hiltRule.inject()
        weatherRepository = WeatherRepositoryImpl(weatherService)
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