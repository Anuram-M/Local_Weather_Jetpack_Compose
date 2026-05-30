package com.ram.core_database

import com.ram.core_database.repository.CurrentWeatherAndForecastRepository
import com.ram.core_database.repository.WeatherHistoryRepository
import com.ram.core_database.repositoryimpl.CurrentWeatherRepositoryImpl
import com.ram.core_database.repositoryimpl.WeatherHistoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideWeatherRepository(
        currentWeatherRepositoryImpl: CurrentWeatherRepositoryImpl
    ): CurrentWeatherAndForecastRepository

    @Binds
    @Singleton
    abstract fun provideWeatherHistoryRepository(
        weatherHistoryRepositoryImpl: WeatherHistoryRepositoryImpl
    ): WeatherHistoryRepository
}