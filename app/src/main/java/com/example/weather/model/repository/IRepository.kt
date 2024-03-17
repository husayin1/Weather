package com.example.weather.model.repository

import androidx.lifecycle.LiveData
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepository {
    //remoteDataSource
    suspend fun getCurrentWeatherData(
        lat: Double,
        long: Double,
        language: String
    ): Flow<WeatherResponse>

    //sharedPref
    fun putBooleanInSharedPref(key: String, value: Boolean)
    fun getBooleanFromSharedPref(key: String, defaultValue: Boolean): Boolean
    fun putStringInSharedPref(key: String, value: String)
    fun getStringFromSharedPref(key: String, defaultValue: String): String

    //SavedLocations
    fun getAllSavedLocations(): Flow<List<SavedLocations>>

    suspend fun saveLocation(location: SavedLocations)
    suspend fun deleteLocation(location: SavedLocations)

    //Alerts
    fun getAllAlerts(): LiveData<List<AlertPojo>>
    suspend fun insertAlert(alert: AlertPojo)
    suspend fun deleteAlert(alert: AlertPojo)
    //HomeData
    suspend fun getWeatherDataFromDB():WeatherResponse?
    suspend fun updateWeatherData(weatherResponse: WeatherResponse)

    fun getAlertWithId(id: String): AlertPojo
}