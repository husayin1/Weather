package com.example.weather.model.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather.db.FakeLocalDataSource
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.FakeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository(
    private var remoteDataSource: FakeRemoteDataSource,
    private var sp:SharedPreferences?=null,
    private var localDataSource: FakeLocalDataSource

    ) : IRepository {
        var locations : MutableList<SavedLocations>?= mutableListOf()
    override suspend fun getCurrentWeatherData(
        lat: Double,
        long: Double,
        language: String
    ): Flow<WeatherResponse> {
        return flowOf(remoteDataSource.getWeatherDataOnline(lat,long,language))
    }

    override fun putBooleanInSharedPref(key: String, value: Boolean) {
        sp?.edit()?.putBoolean(key,value)?.apply()
    }

    override fun getBooleanFromSharedPref(key: String, defaultValue: Boolean): Boolean {
        return sp?.getBoolean(key,defaultValue)?:defaultValue
    }

    override fun putStringInSharedPref(key: String, value: String) {
        sp?.edit()?.putString(key,value)?.apply()
    }

    override fun getStringFromSharedPref(key: String, defaultValue: String): String {
        return sp?.getString(key,defaultValue)?:defaultValue
    }

    override fun getAllSavedLocations(): Flow<List<SavedLocations>> {
        return flowOf(locations as List<SavedLocations>)
    }

    override suspend fun saveLocation(location: SavedLocations) {
        locations?.add(location)
    }

    override suspend fun deleteLocation(location: SavedLocations) {
        locations?.remove(location)
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

    override suspend fun getWeatherDataFromDB(): WeatherResponse {
        return localDataSource.getWeatherDataFromDB()
    }

    override suspend fun updateWeatherData(weatherResponse: WeatherResponse) {
        localDataSource.updateWeatherData(weatherResponse)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return AlertPojo("0",0.0,0.0,0,0,"not")
    }
}