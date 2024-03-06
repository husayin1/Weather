package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.model.response.repos.Repository
import com.example.weather.network.RemoteDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

const val PERMISSION_ID = 9009

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var myFusedLocationProviderClient: FusedLocationProviderClient
    private var location :Boolean=false

    companion object{
        private var remoteDataSource: RemoteDataSource? = null
        private var sharedPreferences : SharedPreferences? = null
        private val repository by lazy { Repository(remoteDataSource!!, sharedPreferences!!) }
        @Synchronized
        fun getInstanceRepository(): Repository {
            if (repository == null) {
                throw IllegalStateException("Repository not initialized")
            }
            return repository!!
        }

        @Synchronized
        fun getInstanceSharedPreferences(): SharedPreferences {
            if (sharedPreferences == null) {
                throw IllegalStateException("SharedPreferences not initialized")
            }
            return sharedPreferences!!
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remoteDataSource = RemoteDataSource.getInstance()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAppBar()
        initViewModel()
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
    fun initViewModel(){
        val viewModelFactory = MainViewModelFactory(
            Repository.getInstance(RemoteDataSource.getInstance(),
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            )
        )
        mainViewModel =ViewModelProvider(this,viewModelFactory).get(MainViewModel::class.java)

    }

    fun initAppBar(){
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        setSupportActionBar(binding.appBarMain.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onResume() {
        super.onResume()
        getLocation()
    }


    private fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if(grantResults.size>2){

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //entrypoint --> onStart or on onResume
    @SuppressLint("MissingPermission")
    private fun getLocation(): Unit {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestNewLocationData()//fresh location
            } else {
                Toast.makeText(this, "Turn on Location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(0)
        myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                if(location==false){
                    val mLastLocation: Location? = p0.lastLocation
                    Log.i("TAG", "onLocationResult: ${getAddress(mLastLocation?.latitude?:31.15,mLastLocation?.longitude?:32.17)} ")
                    location = true
                }
            }
        }
        myFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list?.get(0)?.getAddressLine(0) ?: "0"
    }

}