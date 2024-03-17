package com.example.weather.ui.location.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentMapBinding
import com.example.weather.model.response.LocationLatLngPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.ui.location.ILocation
import com.example.weather.ui.location.LocationManager
import com.example.weather.ui.location.viewmodel.LocationViewModel
import com.example.weather.ui.location.viewmodel.LocationViewModelFactory
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.NetworkManager
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class LocationFragment : Fragment(), ILocation {

    private val TAG = "MapFragment---> "
    private val PERMISSION_ID = 9009

    private lateinit var googleMap: GoogleMap
    private lateinit var binding: FragmentMapBinding
    private var longitude: Double = 31.15
    private var latitude: Double = 32.17
    private var mAddress: String = ""
    lateinit var geocoder: Geocoder
    private var isLocationSelected = false
    private var isCleared = false
    private val args: LocationFragmentArgs by navArgs()
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        val locationViewModelFactory = LocationViewModelFactory(MyApplication.getRepository())
        locationViewModel = ViewModelProvider(this, locationViewModelFactory).get(LocationViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationManager = LocationManager(this, requireContext())
        locationManager.checkPermissionsAndLocation()

        initUI()

        prepareSearchET()

        initMapFrag()

        activateButton()
        activateCurrentLocation()
        activateSearchIcon()
        clearET()
        insertToFav()


    }


    @SuppressLint("MissingPermission")
    private fun setUpMapUsingLocationString(location: String) {
        isLocationSelected = true
        if (isAdded) {
            val addresses = geocoder.getFromLocationName(location, 1)
            Log.i(TAG, "setUpMapUsingLocationString: $addresses")
            if (addresses != null && addresses.isNotEmpty()) {
                    latitude = addresses[0].latitude
                    longitude = addresses[0].longitude

                    setAddressFromLatAndLon(latitude, longitude)
                    val latLng=LatLng(latitude,longitude)
                    moveMap(latLng)
            }
        }
    }

    private fun activateCurrentLocation() {
        binding.currentLocation.setOnClickListener {
            if (NetworkManager.isInternetConnected()) {
                if (locationManager.isLocationEnabled()) {
                    locationManager.getLocation()
                } else {
                    Toast.makeText(requireContext(), R.string.turn_location, Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), R.string.internet_connection, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun activateSearchIcon() {
        binding.searchLocation.setOnClickListener {
            if (NetworkManager.isInternetConnected()) {
                Log.i(TAG, "activateSearchIcon: Search ET is  Blank")
                if (binding.etSearchMap.text.isNotBlank()) {
                    Log.i(TAG, "activateSearchIcon: Search ET is Not Blank")
                    setUpMapUsingLocationString(binding.etSearchMap.text.toString())
                    isLocationSelected = true
                }
            } else {
                Log.i(TAG, "activateSearchIcon: SearchIconElse")
                Toast.makeText(requireContext(), R.string.internet_connection, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun clearET() {
        binding.cancelSearch.setOnClickListener {
            Log.i(TAG, "clearET: Clearing Search Text")
            isCleared = true
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
        }
    }

    fun dealFromFav(){
        if (NetworkManager.isInternetConnected()) {
            if (isLocationSelected) {
                isLocationSelected = false
                locationViewModel.getLocationWeatherData(
                    latitude, longitude
                )
                Toast.makeText(requireContext(), R.string.insert_fav, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), R.string.exist, Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                R.string.internet_connection,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    fun dealFromSettings(){
        if (NetworkManager.isInternetConnected()) {
            locationViewModel.putStringInSharedPref(
                CONSTANTS.sharedPreferencesKeyLatitude,
                latitude.toString()
            )
            locationViewModel.putStringInSharedPref(
                CONSTANTS.sharedPreferencesKeyLongitude,
                longitude.toString()
            )
            Toast.makeText(requireContext(), R.string.selected, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                requireContext(),
                R.string.internet_connection,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    fun dealFromAlerts(){
        if (isLocationSelected) {
            isLocationSelected = false
            val locationLatLngPojo = LocationLatLngPojo("nave",latitude,longitude)
            val action = LocationFragmentDirections.actionMapFragmentToNavAlert(latLng = locationLatLngPojo)
            Navigation.findNavController(requireView()).navigate(action)
            /*
            locationViewModel.putStringInSharedPref(
                "ALERT_LATITUDE_FROM_MAP",
                latitude.toString()
            )
            locationViewModel.putStringInSharedPref(
                "ALERT_LONGITUDE_FROM_MAP",
                longitude.toString()
            )
            locationViewModel.putStringInSharedPref("ALERT_ADDRESS", mAddress)
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.selected), Toast.LENGTH_SHORT)
                .show()*/
        } else {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.select_location),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun activateButton() {
        if (args.isFromFavorite) {
            binding.btnSelectOnMap.visibility = View.VISIBLE
            binding.btnSelectOnMap.setOnClickListener {
                dealFromFav()
            }
        } else if (args.isFromSettings) {
            binding.btnSelectOnMap.setOnClickListener {
             dealFromSettings()
            }
        } else if (args.isFromAlerts) {
            binding.btnSelectOnMap.visibility = View.VISIBLE
            binding.btnSelectOnMap.setOnClickListener {
                dealFromAlerts()
            }
        }
    }

    private fun insertToFav() {
        lifecycleScope.launch {
            locationViewModel.weatherData.collectLatest {
                when (it) {
                    is RetrofitStateWeather.Loading -> {
                        Toast.makeText(requireContext(), R.string.loading, Toast.LENGTH_SHORT)
                            .show()
                    }

                    is RetrofitStateWeather.OnFail -> {
                        Toast.makeText(
                            requireContext(),
                            it.errorMessage.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    is RetrofitStateWeather.OnSuccess -> {
                        setAddressFromLatAndLon(latitude,longitude)
                        locationViewModel.insertToFavorite(
                            SavedLocations(
                                address = mAddress,
                                latitude = latitude,
                                longitude = longitude,
                                currentDescription = it.weatherResponse.current?.weather?.get(0)?.main,
                                currentTemp = it.weatherResponse.current?.temp,
                                lastCheckedTime = System.currentTimeMillis(),
                                icon = it.weatherResponse.current?.weather?.get(0)?.icon,
                                latLngString = (latitude.toString() + longitude.toString())
                            )
                        )
                        Log.i(TAG, "insertToFav: ${System.currentTimeMillis()}")
                        Log.i(TAG, "insertToFav: $mAddress")
                        Toast.makeText(requireContext(), R.string.insert_fav, Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }

    private fun prepareSearchET() {
        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                if (NetworkManager.isInternetConnected()) {
                    binding.etSearchMap.isEnabled = true
                    binding.cancelSearch.isEnabled=true
                } else {
                    Log.i(TAG, "prepareSearchET: ET Is not working")
                    binding.etSearchMap.isEnabled = false
                    binding.cancelSearch.isEnabled=false

                }
                delay(300)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
    }

    override fun onDestroy() {
        super.onDestroy()
        arguments?.putBoolean("isFromSettings", false)
        arguments?.putBoolean("isFromFavorite", false)
        arguments?.putBoolean("isFromAlerts", false)
    }

    override fun mapLocationResult(locationResult: LocationResult) {
        binding.btnSelectOnMap.isEnabled = true
        isLocationSelected = true
        val mLastLocation: Location = locationResult.lastLocation!!
        longitude = mLastLocation.longitude
        latitude = mLastLocation.latitude
        val latLng=LatLng(latitude,longitude)
        setAddressFromLatAndLon(latitude, longitude)
        convertLatLonToAddressAndSetSearchText(latLng)
        moveMap(latLng)
    }

    private fun moveMap(latLng:LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        googleMap.clear()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f), 1500, null)
        googleMap.addMarker(markerOptions)
    }

    private fun initUI() {
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        longitude =
            locationViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLongitude, "")
                .toDouble()
        latitude =
            locationViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLatitude, "")
                .toDouble()

        if (latitude != 0.0) {
            setAddressFromLatAndLon(latitude, longitude)
        }

        val animator = ObjectAnimator.ofFloat(binding.currentLocation, "rotation", 0f, 360f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = 2000
        animator.start()
    }

    private fun initMapFrag() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            googleMap.setOnMapClickListener { latLng ->
                if (NetworkManager.isInternetConnected()) {
                    setOnMapClick(latLng)
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.internet_connection,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (locationManager.isLocationEnabled()) {
            if (NetworkManager.isInternetConnected()) {
                locationManager.getLocation()
            } else {
                Toast.makeText(context, R.string.internet_connection, Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.i(TAG, "initMapFrag: RequestPermission")
            mapRequestPermission()
        }
    }

    private fun setOnMapClick(latLng:LatLng){
        isLocationSelected = true
        moveMap(latLng)
        convertLatLonToAddressAndSetSearchText(latLng)
    }

    private fun convertLatLonToAddressAndSetSearchText(
        latLng:LatLng
    ) {
        latitude=latLng.latitude
        longitude=latLng.longitude
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        Log.i(TAG, "convertLatLonToAddressAndSetSearchText: $addresses")
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            Log.i(TAG, "convertLatLonToAddressAndSetSearchText: $address")
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable(address)
        }
    }

    private fun setAddressFromLatAndLon(latitude: Double, longitude: Double) {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val addressName =
                address.locality ?: address.adminArea ?: "${R.string.unknown}"
            mAddress = addressName
            Log.i(TAG, "setAddressFromLatAndLon: $mAddress")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: Permission Granted")
            }
        }
    }

    override fun mapRequestPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


}
