/*
package com.example.weather

import android.app.Application
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import com.example.weather.model.response.repos.Repository
import com.example.weather.network.RemoteDataSource

class WeatherApp:Application() {
    */
/*companion object{
        private var remoteDataSource: RemoteDataSource? = null
        private var sharedPreferences : SharedPreferences? = null
        private val repository by lazy { Repository(remoteDataSource!!, sharedPreferences!!) }
        @Synchronized
        fun getInstanceRepository(): Repository {
            if (repository == null) {
                throw IllegalStateException("Repository not initialized")
            }
            return repository!!
        }

        @Synchronized
        fun getInstanceSharedPreferences(): SharedPreferences {
            if (sharedPreferences == null) {
                throw IllegalStateException("SharedPreferences not initialized")
            }
            return sharedPreferences!!
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
        //MyHelper.createSharedPreferencesInstance(this)
        remoteDataSource = RemoteDataSource.getInstance()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }*//*

}*/
