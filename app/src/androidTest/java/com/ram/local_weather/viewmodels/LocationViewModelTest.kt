package com.ram.local_weather.viewmodels

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.ram.core_database.repository.CurrentWeatherAndForecastRepository
import com.ram.core_database.repositoryimpl.CurrentWeatherRepositoryImpl
import com.ram.core_domain.usecase.GetForecastUseCase
import com.ram.core_domain.usecase.GetLocationDataUseCase
import com.ram.core_domain.usecase.GetWeatherUseCase
import com.ram.core_network.WeatherRepositoryImpl
import com.ram.local_weather.UILOGIC_STATE
import com.ram.local_weather.util.CheckerUtil
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit
import javax.inject.Inject

@HiltAndroidTest
class LocationViewModelTest {

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExcutorRule = InstantTaskExecutorRule()

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)


    val application: Application = Application()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var weatherRepository: WeatherRepositoryImpl


    lateinit var viewModel: LocationViewModel


    val mContext = ApplicationProvider.getApplicationContext<Context>()

    lateinit var mockChecker: CheckerUtil

    lateinit var getWeatherUseCase: GetWeatherUseCase

    lateinit var getForecastUseCase: GetForecastUseCase

    lateinit var getLocationDataUseCase: GetLocationDataUseCase

    lateinit var currentWeatherRepository: CurrentWeatherAndForecastRepository

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        mockChecker = mock(CheckerUtil::class.java)
        getWeatherUseCase = mock(GetWeatherUseCase(weatherRepository))
        getForecastUseCase = mock(GetForecastUseCase(weatherRepository))
        getLocationDataUseCase = mock(GetLocationDataUseCase(weatherRepository))
        currentWeatherRepository = mock(CurrentWeatherRepositoryImpl())
        viewModel = LocationViewModel(
            fusedLocationProviderClient,
            application,
            getWeatherUseCase,
            getForecastUseCase,
            getLocationDataUseCase,
            mockChecker,
            currentWeatherRepository
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun checkAppStateNoPermission() {
        Mockito.`when`(mockChecker.checkLocationPermission(application))
            .thenReturn(false)
        Mockito.`when`(mockChecker.checkLocationEnabled(application))
            .thenReturn(false)
        val result = viewModel.checkAppState()
        Assert.assertEquals(UILOGIC_STATE.LOGIC_PERMISSION_NEEDED, result)
    }

//    @Test
//    fun checkAppStateLocationToggle() {
//        Mockito.`when`(mockChecker.checkLocationPermission(application))
//            .thenReturn(true)
//        Mockito.`when`(mockChecker.checkLocationEnabled(application))
//            .thenReturn(false)
//        val result = viewModel.checkAppState()
//        Assert.assertEquals(UILOGIC_STATE.LOGIC_LOCATION_NEEDED, result)
//    }

    @Test
    fun checkAppStateAppReady() {
        Mockito.`when`(mockChecker.checkLocationPermission(application))
            .thenReturn(true)
        Mockito.`when`(mockChecker.checkLocationEnabled(application))
            .thenReturn(true)
        val result = viewModel.checkAppState()
        Assert.assertEquals(UILOGIC_STATE.LOGIC_APP_READY, result)
    }

    @Test
    fun updatePermissionProvidedTest() {
        val result = viewModel.updatePermission(true)
        Assert.assertEquals(true, result)
    }
}