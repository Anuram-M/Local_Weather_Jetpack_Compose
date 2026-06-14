package com.ram.local_weather.modules

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlanceWidgetModule {

    @Singleton
    @Provides
    fun provideGlanceManager(@ApplicationContext context: Context) : GlanceAppWidgetManager {
        return GlanceAppWidgetManager(context)
    }
}