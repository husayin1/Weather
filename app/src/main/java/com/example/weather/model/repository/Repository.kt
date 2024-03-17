package com.example.weather.model.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.weather.db.ILocalDataSource
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.IRemoteDataSource
import com.example.weather.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository(
    private val remoteDataSource: IRemoteDataSource,
    private val sharedPreferences: SharedPreferences,
    private val localDataSource: ILocalDataSource
) : IRepository {
    //shared pref
    private val sharedPreferencesEditor = sharedPreferences.edit()

    //singleton
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        @Synchronized
        fun getInstance(
            remoteDataSource: RemoteDataSource,
            sharedPreferences: SharedPreferences,
            localDataSource: ILocalDataSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(remoteDataSource, sharedPreferences, localDataSource)
                INSTANCE = instance
                instance
            }
        }
    }

    //remoteDataSource
    override suspend fun getCurrentWeatherData(
        lat: Double,
        long: Double,
        language: String
    ) = flow {
        emit(remoteDataSource.getWeatherDataOnline(lat, long, language))
    }

    //sharePref
    override fun putBooleanInSharedPref(key: String, value: Boolean) {
        sharedPreferencesEditor.putBoolean(key, value)
        sharedPreferencesEditor.apply()
    }

    override fun getBooleanFromSharedPref(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun putStringInSharedPref(key: String, value: String) {
        sharedPreferencesEditor.putString(key, value)
        sharedPreferencesEditor.apply()
    }

    override fun getStringFromSharedPref(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key,defaultValue)?:""
    }



    override fun getAllSavedLocations(): Flow<List<SavedLocations>>{
        return localDataSource.getAllSavedLocations()
    }

    override suspend fun saveLocation(location: SavedLocations) {
        localDataSource.saveLocation(location)
    }

    override suspend fun deleteLocation(location: SavedLocations) {
        localDataSource.deleteLocation(location)
    }


    override fun getAllAlerts(): LiveData<List<AlertPojo>> {
        return localDataSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        localDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        localDataSource.deleteAlert(alert)
    }

    //Home Data
    override suspend fun getWeatherDataFromDB(): WeatherResponse? {
        return localDataSource.getWeatherDataFromDB()
    }

    override suspend fun updateWeatherData(weatherResponse: WeatherResponse) {
        localDataSource.updateWeatherData(weatherResponse)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return localDataSource.getAlertWithId(id)
    }
}