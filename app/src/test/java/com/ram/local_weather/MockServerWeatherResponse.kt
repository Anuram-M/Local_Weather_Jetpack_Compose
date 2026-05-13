package com.ram.local_weather

import com.ram.local_weather.networkservices.WeatherService
import com.ram.local_weather.util.JsonHelper
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MockServerWeatherResponse {


    lateinit var mockWebServer: MockWebServer
    lateinit var weatherService: WeatherService
    lateinit var retrofit: Retrofit


    @Before
    fun setUp() {
      mockWebServer = MockWebServer()
      retrofit = Retrofit.Builder().baseUrl(mockWebServer.url("/")).addConverterFactory(GsonConverterFactory.create()).build()
      weatherService = retrofit.create(WeatherService::class.java)
    }


    @Test
    fun getWeatherDataEmptyResponseTest() = runTest{
        val mockResponse = MockResponse()
        mockResponse.setBody("{}")
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        val respone = weatherService.getWeatherData(10.0,23.9, "","")
        mockWebServer.takeRequest()
        Assert.assertEquals(200, respone.code())
    }


    @Test
    fun getWeatherDataResponseFromResourceTest() = runTest {
        val mockResponse = MockResponse()
        val jsonResponse = JsonHelper.getJson("/forecast.json")
        mockResponse.setBody(jsonResponse)
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        val result = weatherService.getForeCastData(10.0, 23.0, "", "")
        mockWebServer.takeRequest()
        Assert.assertEquals(200, result.code())
    }

    @Test
    fun getWeatherDataReducedResponseFromResourceTest() = runTest {
        val mockResponse = MockResponse()
        val jsonResponse = JsonHelper.getJson("/forecastResponse2Items.json")
        mockResponse.setBody(jsonResponse)
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)
        val result = weatherService.getForeCastData(10.0, 23.0, "", "")
        mockWebServer.takeRequest()
        Assert.assertEquals(200, result.code())
        Assert.assertEquals(2, result.body()?.list?.size)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

}