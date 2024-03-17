package com.example.weather.model.response

import androidx.annotation.NonNull
import androidx.room.Entity
@Entity(tableName = "WeatherData", primaryKeys = ["lat","lon"])
data class WeatherResponse(
    val alerts: List<Alerts?>? = null,
    val current: Current? = null,
    val daily: List<DailyItem?>? = null,
    @NonNull
    val lon: Double,
    val hourly: List<HourlyItem?>? = null,
    @NonNull
    val lat: Double,
    val addressName: String?
)
