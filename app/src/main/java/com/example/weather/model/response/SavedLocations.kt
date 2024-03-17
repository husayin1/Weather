package com.example.weather.model.response

import androidx.annotation.NonNull
import androidx.room.Entity

@Entity(tableName = "SavedLocationsTable", primaryKeys = ["latLngString"])
data class SavedLocations(
    @NonNull
    val latitude: Double,
    @NonNull
    val longitude: Double,
    val address: String,
    val latLngString: String,
    val currentTemp: Double?,
    val currentDescription: String?,
    val lastCheckedTime: Long,
    val icon: String?
)