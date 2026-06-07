package com.ram.local_weather.modules

import android.content.Context
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AgeSignalModule {

    @Provides
    @Singleton
    fun provideAgeSignalsProvider(@ApplicationContext context: Context) : AgeSignalsManager {
        return AgeSignalsManagerFactory.create(context)
    }
}