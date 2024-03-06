package com.example.weather.network

import com.example.weather.model.response.WeatherResponse
import com.example.weather.utilites.CONSTANTS

class RemoteDataSource : IRemoteDataSource {
    companion object{
        @Volatile
        private var remoteDataSourceInstance:RemoteDataSource?=null
        @Synchronized
        fun getInstance():RemoteDataSource{
            return RemoteDataSource.remoteDataSourceInstance?: synchronized(this){
                val instance = RemoteDataSource()
                RemoteDataSource.remoteDataSourceInstance=instance
                instance
            }
        }
    }
    private val weatherApiService=RetrofitHelper.retrofitInstance
    override suspend fun getWeatherDataOnline(
        lat: Double,
        lon: Double,
        language: String
    ): WeatherResponse {
        return weatherApiService.getWeatherResponse(lat,lon,language, units = CONSTANTS.celsius, apiKey = CONSTANTS.appKey)
    }
}