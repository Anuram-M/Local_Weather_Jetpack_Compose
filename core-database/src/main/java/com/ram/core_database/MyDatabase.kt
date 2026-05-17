package com.ram.core_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.ram.core_database.dao.WeatherDao
import com.ram.core_database.entiry.CurrentWeather
import com.ram.core_database.util.MyTypeConvertor

@TypeConverters(MyTypeConvertor::class)
@Database(entities = [CurrentWeather::class], version = 1, exportSchema = true)
abstract class MyDatabase: RoomDatabase() {
    abstract fun weatherDao() : WeatherDao
    companion object {
        @Volatile
        var INSTANCE: MyDatabase? = null

        fun getInstance(context: Context) : MyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    MyDatabase::class.java,
                    "myDatabase"
                ).build().also { INSTANCE = it }
            }
        }
    }
}