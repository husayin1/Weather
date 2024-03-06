package com.example.weather.model.response.repos

import android.content.SharedPreferences
import com.example.weather.network.IRemoteDataSource
import com.example.weather.network.RemoteDataSource
import kotlinx.coroutines.flow.flow

class Repository(
    private val remoteDataSource: IRemoteDataSource,
    private val sharedPreferences: SharedPreferences
) : IRepository {
    //shared pref
    private val sharedPreferencesEditor =sharedPreferences.edit()
    //singleton
    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        @Synchronized
        fun getInstance(remoteDataSource: RemoteDataSource,sharedPreferences: SharedPreferences): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(remoteDataSource, sharedPreferences)
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
        sharedPreferencesEditor.putBoolean(key,value)
        sharedPreferencesEditor.apply()
    }

    override fun getBooleanFromSharedPref(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key,defaultValue)
    }

    override fun putStringInSharedPref(key: String, value: String) {
        sharedPreferencesEditor.putString(key,value)
        sharedPreferencesEditor.apply()
    }

    override fun getStringFromSharedPref(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key,defaultValue)?:"null"
    }

}