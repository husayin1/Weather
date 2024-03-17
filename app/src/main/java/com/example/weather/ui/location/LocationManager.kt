package com.example.weather.ui.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weather.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationManager(private val imap: ILocation, private val context: Context) {
    private val TAG = "MAP MANAGER"
    private lateinit var myFusedLocationProviderClient: FusedLocationProviderClient
    private var ignore =false

    //entrypoint --> onStart or on onResume
    @SuppressLint("MissingPermission")
    fun getLocation(): Unit {
        if (checkPermission()) {
            Log.i(TAG, "getLocation: CheckPermissionsGranted")
            if (isLocationEnabled()) {
                Log.i(TAG, "getLocation: getFreshLocation")
                requestNewLocationData()//fresh location
            } else {
                if(!ignore){

                    Log.i(TAG, "getLocation: Location is not enable")
                    Toast.makeText(context, R.string.turn_location, Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                    ignore=true
                }else{
                    Toast.makeText(context,"Go to settings and open location",Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "getLocation: Go To Settings")
                }
            }
        } else {
//            requestPermissions()
            Log.i(TAG, "getLocation: permission denied")
            imap.mapRequestPermission()

        }
    }
    @SuppressLint("MissingPermission")
    fun checkPermissionsAndLocation(): Unit {
        if (checkPermission()) {
            Log.i(TAG, "checkPermissionsAndLocation: Permission granted")
            if (isLocationEnabled()) {
                Log.i(TAG, "checkPermissionsAndLocation: Location is Enabled")
            } else {
                if(!ignore){
                    Toast.makeText(context, R.string.turn_location, Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                    ignore=true
                }else{
                    Toast.makeText(context,"Go to settings and open location",Toast.LENGTH_SHORT).show()
                    Log.i(TAG, "getLocation: Go To Settings")
                }
            }
        } else {
            Log.i(TAG, "checkPermissionsAndLocation: Permission Denied")
            imap.mapRequestPermission()

        }
    }
    private fun checkPermission(): Boolean {
        Log.i(TAG, "checkPermission: asking for permissions")
        val result = ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return result
    }
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(0)
        myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                myFusedLocationProviderClient.removeLocationUpdates(this)
                Log.i(TAG, "onLocationResult: ${locationResult.lastLocation}")
                imap.mapLocationResult(locationResult)
            }
        }
        myFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

}