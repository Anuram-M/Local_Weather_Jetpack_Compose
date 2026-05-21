package com.ram.local_weather.modules

import com.google.android.gms.location.FusedLocationProviderClient
import com.ram.core_network.WeatherService
import com.ram.core_network.modules.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.mockito.kotlin.mock
import javax.inject.Singleton


@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [LocationModule::class, NetworkModule::class])
object TestLocationModule {

    @Provides
    @Singleton
    fun provideWeatherService(): WeatherService = mock()

    @Provides
    @Singleton
    fun returnFusedLocationProviderClient(): FusedLocationProviderClient = mock()

}