package com.example.weather.network

import com.example.weather.model.response.WeatherResponse

interface IRemoteDataSource {
    suspend fun getWeatherDataOnline(lat: Double, lon: Double, language: String): WeatherResponse
}