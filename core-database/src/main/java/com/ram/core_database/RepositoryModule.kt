package com.ram.core_database

import com.ram.core_database.repository.CurrentForecastRepository
import com.ram.core_database.repository.CurrentWeatherRepository
import com.ram.core_database.repositoryimpl.CurrentForecastRepositoryImpl
import com.ram.core_database.repositoryimpl.CurrentWeatherRepositoryImpl
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
    ): CurrentWeatherRepository

    @Binds
    @Singleton
    abstract fun provideForecastRepository(
        currentForecastRepositoryImpl: CurrentForecastRepositoryImpl
    ): CurrentForecastRepository
}