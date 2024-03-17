package com.example.weather.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(
    private var savedLocations: MutableList<SavedLocations> = mutableListOf(),
    private var weather: WeatherResponse?=null,
) : ILocalDataSource {
    override fun getAllSavedLocations(): Flow<List<SavedLocations>> {
        return flowOf(savedLocations)
    }

    override suspend fun saveLocation(locations: SavedLocations) {
        savedLocations.add(locations)
    }

    override suspend fun deleteLocation(locations: SavedLocations) {
        savedLocations.remove(locations)
    }

    override fun getAllAlerts(): LiveData<List<AlertPojo>> {
        return MutableLiveData()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
//        alerts.add(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
//        alerts.remove(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherDataFromDB(): WeatherResponse {
        return weather?:throw Exception("Null Values")
    }

    override suspend fun updateWeatherData(weatherRespon: WeatherResponse) {
        weather=weatherRespon
    }
}