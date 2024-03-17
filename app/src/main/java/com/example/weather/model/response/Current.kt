package com.example.weather.model.response

data class Current(
    val dt: Long? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null,
    val temp: Double? = null,
    val feels_like: Any? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val dew_point: Any? = null,
    val uvi: Double? = null,
    val clouds: Int? = null,
    val visibility: Int? = null,
    val wind_speed: Double? = null,
    val wind_deg: Int? = null,
    val weather: List<WeatherItem?>? = null,
)
