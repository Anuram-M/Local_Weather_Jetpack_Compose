package com.ram.core_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.ram.core_database.entity.CurrentForecast

@Dao
interface ForecastDao {

    @Insert(onConflict = REPLACE)
    fun insertForecast(currentForecast: CurrentForecast)

    @Update
    fun updateForecast(currentForecast: CurrentForecast)

    @Query("select * from current_forecast")
    fun getForecast(): CurrentForecast?
}