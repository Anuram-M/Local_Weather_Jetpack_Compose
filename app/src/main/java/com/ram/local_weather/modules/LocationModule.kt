package com.ram.local_weather.modules

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ram.local_weather.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideMyApplication(@ApplicationContext context: Context): MyApplication {
        return context.applicationContext as MyApplication
    }

    @Provides
    @Singleton
    fun locationModule(
        @ApplicationContext context: Context
    ) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}