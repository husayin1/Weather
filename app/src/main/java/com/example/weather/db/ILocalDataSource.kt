package com.example.weather.db

import androidx.lifecycle.LiveData
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface ILocalDataSource {
    //Home Data
    suspend fun getWeatherDataFromDB(): WeatherResponse?
    suspend fun updateWeatherData(weatherResponse: WeatherResponse)

    //Saved Locations
    fun getAllSavedLocations(): Flow<List<SavedLocations>>
    suspend fun saveLocation(locations: SavedLocations): Unit
    suspend fun deleteLocation(locations: SavedLocations): Unit

    //Alerts
    fun getAllAlerts(): LiveData<List<AlertPojo>>
    suspend fun insertAlert(alert: AlertPojo): Unit
    suspend fun deleteAlert(alert: AlertPojo): Unit
    fun getAlertWithId(id: String): AlertPojo
}