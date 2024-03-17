package com.example.weather.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weather.db.dao.AlertsDao
import com.example.weather.db.dao.SavedLocationsDao
import com.example.weather.db.dao.WeatherDao
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSource private constructor(context: Context) : ILocalDataSource {
    private val TAG = "LocalDataSource"

    private var savedLocationsDao: SavedLocationsDao
    private var alertsDao: AlertsDao
    private var weatherDao: WeatherDao

    init {
        val db = DataBase.getInstance(context.applicationContext)
        savedLocationsDao = db.getSavedLocationsDao()
        alertsDao = db.getAlertsDao()
        weatherDao = db.getWeatherDao()
    }

    companion object {
        @Volatile
        private var localDataSourceInstance: LocalDataSource? = null

        @Synchronized
        fun getInstance(context: Context): LocalDataSource {
            return localDataSourceInstance ?: synchronized(this) {
                val instance = LocalDataSource(context)
                localDataSourceInstance = instance
                instance
            }
        }
    }

    //Saved Locations
    override fun getAllSavedLocations(): Flow<List<SavedLocations>>{
        Log.i("TAG", "getAllFavLocations: ")
        return savedLocationsDao.getAllFavLocations()
    }

    override suspend fun saveLocation(locations: SavedLocations): Unit {
        savedLocationsDao.saveLocation(locations)
    }

    override suspend fun deleteLocation(locations: SavedLocations): Unit {
        savedLocationsDao.deleteLocation(locations)
    }
    //Alerts
    override fun getAllAlerts(): LiveData<List<AlertPojo>> {
        return alertsDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: AlertPojo) {
        alertsDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: AlertPojo) {
        alertsDao.deleteAlert(alert)
    }

    override fun getAlertWithId(id: String): AlertPojo {
        return alertsDao.getAlertWithId(id)
    }

    //Home Data
    override suspend fun getWeatherDataFromDB(): WeatherResponse? {
        Log.i(TAG, "getWeatherDataFromDB: retrieving data from db for home")
        return weatherDao.getWeatherDataFromDB()
    }
    //update home data
    override suspend fun updateWeatherData(weatherResponse: WeatherResponse) {
        weatherDao.updateWeatherData(weatherResponse)
    }
}