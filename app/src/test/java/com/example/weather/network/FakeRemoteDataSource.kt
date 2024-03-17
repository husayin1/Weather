package com.example.weather.network

import com.example.weather.model.response.WeatherResponse

class FakeRemoteDataSource(
    private var weatherResponse: WeatherResponse,
) : IRemoteDataSource {
    override suspend fun getWeatherDataOnline(
        lat: Double,
        lon: Double,
        language: String
    ): WeatherResponse {
        return weatherResponse
    }

}