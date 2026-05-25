package com.ram.core_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ram.core_database.dao.ForecastDao
import com.ram.core_database.dao.WeatherDao
import com.ram.core_database.entity.CurrentForecast
import com.ram.core_database.entity.CurrentWeather
import com.ram.core_database.entity.SearchedWeather
import com.ram.core_database.util.ForecastConvertor
import com.ram.core_database.util.MyTypeConvertor

@TypeConverters(MyTypeConvertor::class, ForecastConvertor::class)
@Database(
    entities = [CurrentWeather::class, SearchedWeather::class, CurrentForecast::class],
    version = 2,
    exportSchema = true
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forecastDao(): ForecastDao

    companion object {
        @Volatile
        var INSTANCE: MyDatabase? = null

        val migration1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("create table if not exists `current_forecast` ( `id` integer primary key not null, `data` text not null )")
            }
        }

        fun getInstance(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    MyDatabase::class.java,
                    "myDatabase"
                ).addMigrations(migration1_2)
                    .build().also { INSTANCE = it }
            }
        }
    }
}