package com.example.weather.network

import com.example.weather.model.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    suspend fun getWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ):WeatherResponse
}