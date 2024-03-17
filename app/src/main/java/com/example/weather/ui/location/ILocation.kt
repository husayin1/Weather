package com.example.weather.ui.location

import com.google.android.gms.location.LocationResult

interface ILocation {
    fun mapRequestPermission()
    fun mapLocationResult(locationResult: LocationResult)
}