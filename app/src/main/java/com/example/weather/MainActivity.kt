package com.example.weather

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.db.LocalDataSource
import com.example.weather.model.repository.Repository
import com.example.weather.network.RemoteDataSource
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.NetworkManager
import java.util.Locale

/*
const val PERMISSION_ID = 9009
*/

class MainActivity : AppCompatActivity() , NetworkManager.NetworkListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var navController: NavController
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAppBar()
        initViewModel()
        NetworkManager.setListener(this)
//        mainViewModel.activateAlerts(this)
        checkIfLayoutShouldBeEnglish()
        checkIfLayoutShouldBeArabic()
        checkIfThemeChangedBySettings()
    }

    private fun checkIfThemeChangedBySettings() {
        if(mainViewModel.isThemeChangedBySettings()){

            checkIfLayoutShouldBeEnglish()
            mainViewModel.setIsThemeChangedBySettingsToFalse()
        }
    }
    fun initViewModel() {
        val viewModelFactory = MainViewModelFactory(
            Repository.getInstance(
                RemoteDataSource.getInstance(),
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this),
                LocalDataSource.getInstance(this)
            )
        )
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

    }

    override fun onNetworkStatusChanged(isConnected: Boolean) {
        runOnUiThread {
            if(isConnected){
            } else{
                Toast.makeText(this,R.string.internet_connection, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfLayoutShouldBeArabic() {
        if (mainViewModel.isArabic() && !mainViewModel.isLayoutChangedBySettings()) {

            val locale = Locale("ar")

            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        mainViewModel.putBooleanInSharedPref("isLayoutChangedBySettings", false)
    }

    private fun checkIfLayoutShouldBeEnglish() {
        if (!mainViewModel.isArabic()){

            val locale = Locale("en")
            Locale.setDefault(locale)
            val configuration = Configuration()
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
        mainViewModel.putBooleanInSharedPref("isLayoutChangedBySettings", false)
    }

    fun initAppBar() {
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        setSupportActionBar(binding.appBarMain.toolbar)
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home, R.id.nav_fav, R.id.nav_settings, R.id.nav_alert -> {
                    supportActionBar?.show()
                }

                else -> {
                    supportActionBar?.hide()
                }
            }
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_fav, R.id.nav_alert, R.id.nav_settings
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
//        getLocation()
    }


    /*private fun checkPermission(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return result
    }*/
    /*
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
        }*/

    /*private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }*/

    /*//entrypoint --> onStart or on onResume
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
    }*/

    /*@SuppressLint("MissingPermission")
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
    }*/

    /*private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list?.get(0)?.getAddressLine(0) ?: "0"
    }*/

}