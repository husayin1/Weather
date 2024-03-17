package com.example.weather.model.response


data class DailyItem(
    val moonset: Int? = null,
    val rain: Any? = null,
    val sunrise: Int? = null,
    val temp: Temp? = null,
    val moonPhase: Any? = null,
    val uvi: Double? = null,
    val moonrise: Int? = null,
    val pressure: Int? = null,
    val clouds: Int? = null,
    val feelsLike: FeelsLike? = null,
    val windGust: Any? = null,
    val dt: Int? = null,
    val pop: Double? = null,
    val windDeg: Int? = null,
    val dewPoint: Any? = null,
    val sunset: Int? = null,
    val weather: List<WeatherItem?>? = null,
    val humidity: Int? = null,
    val wind_speed: Double? = null
)