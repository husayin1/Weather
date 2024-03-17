package com.example.weather.network

import com.example.weather.model.response.WeatherResponse
import com.example.weather.utilites.CONSTANTS

class RemoteDataSource : IRemoteDataSource {
    companion object{
        @Volatile
        private var remoteDataSourceInstance:RemoteDataSource?=null
        @Synchronized
        fun getInstance():RemoteDataSource{
            return remoteDataSourceInstance?: synchronized(this){
                val instance = RemoteDataSource()
                remoteDataSourceInstance=instance
                instance
            }
        }
    }
    private val service = RetrofitHelper.retrofitInstance.create(ApiService::class.java)
    override suspend fun getWeatherDataOnline(
        lat: Double,
        lon: Double,
        language: String
    ): WeatherResponse {
        return service.getWeatherResponse(apiKey = CONSTANTS.appKey,lat,lon,language)
    }

}