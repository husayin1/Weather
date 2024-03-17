package com.example.weather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.weather.model.response.WeatherResponse

@Dao
interface WeatherDao {
    //Limit 1 to display on Home from db
    @Query("SELECT * FROM WeatherData LIMIT 1")
    suspend fun getWeatherDataFromDB():WeatherResponse?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(data:WeatherResponse)
    @Transaction
    suspend fun updateWeatherData(data:WeatherResponse){
        val updatedData = getWeatherDataFromDB()
        updatedData?.let {
            deleteWeatherData(it)
        }
        insertWeatherData(data)
    }
    @Delete
    suspend fun deleteWeatherData(data:WeatherResponse)

}