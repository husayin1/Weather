package com.example.weather.model.response

data class HourlyItem(
    val dt: Int? = null,
    val temp: Double? = null,
    val feels_like: Any? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val dew_point: Any? = null,
    val uvi: Double? = null,
    val clouds: Int? = null,
    val visibility: Int? = null,
    val wind_speed: Any? = null,
    val wind_deg:Any?=null,
    val wind_gust: Any? = null,
    val weather: List<WeatherItem?>? = null,
)


