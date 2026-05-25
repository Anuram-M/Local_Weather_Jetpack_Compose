package com.ram.core_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.ram.core_database.entity.SearchedWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchedWeatherDao {

    @Insert(onConflict = REPLACE)
    suspend fun insetSearchedWeather(searchedWeather: SearchedWeather)

    @Query("select * from searched_locations")
    fun getSearchedWeather(): Flow<List<SearchedWeather>>

    @Delete
    suspend fun deleteSearch(searchedWeather: SearchedWeather)

    @Query("delete from searched_locations")
    suspend fun deleteHistory()
}