package com.example.weather.model.response.repos

import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepository {
    //remoteDataSource
    suspend fun getCurrentWeatherData(lat:Double,long:Double,language:String):Flow<WeatherResponse>

    //sharedPref
    fun putBooleanInSharedPref(key:String,value:Boolean)
    fun getBooleanFromSharedPref(key:String,defaultValue:Boolean):Boolean
    fun putStringInSharedPref(key:String,value:String)
    fun getStringFromSharedPref(key:String,defaultValue:String):String
}