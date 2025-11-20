package com.ram.local_weather.modules

import com.google.android.gms.location.FusedLocationProviderClient
import com.ram.local_weather.networkservices.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.mockito.kotlin.mock
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [LocationModule::class, NetworkModule::class])
object TestLocationModule {

    //    @Provides
//    @Singleton
//    fun provideMyApplication(@ApplicationContext context: Context): Application {
//        return context.applicationContext as Application
//    }
    @Provides
    @Singleton
    fun provideWeatherService(): WeatherService = mock()

    @Provides
    @Singleton
    fun returnFusedLocationProviderClient(): FusedLocationProviderClient = mock()

}