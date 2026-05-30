package com.ram.core_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ram.core_database.dao.WeatherAndForecastDao
import com.ram.core_database.dao.WeatherHistoryDao
import com.ram.core_database.entity.CurrentWeatherAndForecast
import com.ram.core_database.entity.WeatherHistory
import com.ram.core_database.util.ForecastConvertor
import com.ram.core_database.util.MyTypeConvertor

@TypeConverters(MyTypeConvertor::class, ForecastConvertor::class)
@Database(
    entities = [CurrentWeatherAndForecast::class,
        WeatherHistory::class
    ],
    version = 3,
    exportSchema = true
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherAndForecastDao
    abstract fun weatherHistoryDao(): WeatherHistoryDao

    companion object {
        @Volatile
        var INSTANCE: MyDatabase? = null

        val migration1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("create table if not exists `current_forecast` ( `id` integer primary key not null, `data` text not null )")
            }
        }

        val migration2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `current_forecast`")
                db.execSQL(
                    """
                    create table if not exists `weather_history` ( `place` text primary key not null, `temp` real not null default 0.0, `lastChecked` integer not null default -1)
                """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `current_weather_new` (
                `id` INTEGER PRIMARY KEY NOT NULL,
                `weather` TEXT NOT NULL,
                `forecast` TEXT NOT NULL
            )
            """.trimIndent()
                )

                // 2. Drop the old table completely
                db.execSQL("DROP TABLE IF EXISTS `current_weather`")

                // 3. Rename the new table to your permanent production table name
                db.execSQL("ALTER TABLE `current_weather_new` RENAME TO `current_weather`")
            }
        }

        fun getInstance(context: Context): MyDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    MyDatabase::class.java,
                    "myDatabase"
                ).addMigrations(
                    migration1_2,
                    migration2_3
                ).build().also { INSTANCE = it }
            }
        }
    }
}