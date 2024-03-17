package com.example.weather.network

import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse

sealed class RetrofitStateWeather<out T> {
    data class OnSuccess<T>(val weatherResponse: T): RetrofitStateWeather<T>()
    data class OnFail(val errorMessage: Throwable): RetrofitStateWeather<Nothing>()
    object Loading : RetrofitStateWeather<Nothing>()
}
