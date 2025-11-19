package com.ram.local_weather.repository

import androidx.lifecycle.viewmodel.compose.viewModel
import com.ram.local_weather.NETWORK_RESULT
import com.ram.local_weather.models.Clouds
import com.ram.local_weather.models.Coord
import com.ram.local_weather.models.ForeCastResponse
import com.ram.local_weather.models.Main
import com.ram.local_weather.models.Sys
import com.ram.local_weather.models.Weather
import com.ram.local_weather.models.WeatherResponse
import com.ram.local_weather.models.Wind
import com.ram.local_weather.networkservices.WeatherService
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.kotlin.any
import retrofit2.Response

class WeatherRepositoryTest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var weatherService: WeatherService

    lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        weatherRepository = WeatherRepository(weatherService)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getWeatherForecastDataMockResponse() {
        runTest {
            val queryWeatherResponse = WeatherResponse(
                timezone = 19800,
                id = 1259229,
                name = "Pune",
                cod = 200,
                coord = Coord(lon = 73.8553, lat = 18.5196),
                wind = Wind(speed = 2.33, deg = 86, gust = 3.55),
                clouds = Clouds(0),
                sys = Sys(country = "IN", sunrise = 1763428377, sunset = 1763468807),
                dt = 1763433214,
                visibility = 10000,
                main = Main(
                    temp = 292.09,
                    feels_like = 291.63,
                    temp_min = 291.15,
                    temp_max = 292.09,
                    pressure = 1018,
                    humidity = 61,
                    sea_level = 1018,
                    grnd_level = 944
                ),
                base = "stations",
                weather = listOf(
                    Weather(
                        id = 800,
                        main = "Clear",
                        description = "clear sky",
                        icon = "01d"
                    )
                ),
            )
            Mockito.`when`(
                weatherService.getWeatherDataFromLocation(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString()
                )
            ).thenReturn(Response.success(queryWeatherResponse))
            val result = weatherRepository.getWeatherDataFromLocation("pune")
            Assert.assertEquals("Pune", result.data?.name)
        }
    }

    @Test
    fun getWeatherForecastWrongCityNameTest() {
        runTest {
            Mockito.`when`(
                weatherService.getWeatherDataFromLocation(
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString()
                )
            ).thenReturn(Response.error(404, "city not found".toResponseBody()))
            val errorResult = weatherRepository.getWeatherDataFromLocation("kashmir")
            Assert.assertEquals(true, errorResult is NETWORK_RESULT.Error)
        }
    }

    @Test
    fun getWeatherDataMockRespone() {
        runTest {
            val weatherResponse = WeatherResponse(
                coord = Coord(19.9, 15.0),
                weather = listOf(Weather(1, "Clear", "clear sky", "01d")),
                base = "stations",
                main = Main(
                    temp = 25.0,
                    feels_like = 26.0,
                    temp_min = 24.0,
                    temp_max = 30.0,
                    pressure = 1000,
                    humidity = 60,
                    sea_level = null,
                    grnd_level = null
                ),
                visibility = 10000,
                wind = Wind(2.5, 180, null),
                clouds = Clouds(0),
                dt = 12345678,
                sys = Sys("IN", 123456, 789101),
                timezone = 19800,
                id = 1,
                name = "Z City",
                cod = 200
            )
            Mockito.`when`(
                weatherService.getWeatherData(
                    ArgumentMatchers.anyDouble(),
                    ArgumentMatchers.anyDouble(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString()
                )
            ).thenReturn(
                Response.success(weatherResponse)
            )
            val result = weatherRepository.getWeatherData(19.9, 15.0)
            Assert.assertEquals(true, result is NETWORK_RESULT.Success)
            Assert.assertEquals("Z City", result.data?.name)
            Assert.assertEquals("clear sky", result.data?.weather!![0].description)
            Assert.assertEquals(19.9, result.data?.coord?.lon)
        }
    }


    @Test
    fun getWeatherData() {
        runTest {
            Mockito.`when`(
                weatherService.getWeatherData(
                    ArgumentMatchers.anyDouble(),
                    ArgumentMatchers.anyDouble(),
                    ArgumentMatchers.anyString(),
                    ArgumentMatchers.anyString()
                )
            ).thenReturn(
                Response.error(400, "Something went wrong".toResponseBody())
            )
            val result = weatherRepository.getWeatherData(19.9, 15.0)
            Assert.assertEquals(true, result is NETWORK_RESULT.Error)
        }
    }
}