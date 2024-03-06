package com.example.weather.network

import com.example.weather.model.response.WeatherResponse

sealed class RetrofitStateWeather {
    class OnSuccess(val weatherResponse: WeatherResponse ):RetrofitStateWeather()
    class OnFail(val errorMessage: Throwable ):RetrofitStateWeather()
    object Loading : RetrofitStateWeather()
}
