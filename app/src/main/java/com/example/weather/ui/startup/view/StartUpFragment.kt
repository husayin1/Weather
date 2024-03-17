package com.example.weather.ui.startup.view

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentStartUpBinding
import com.example.weather.ui.location.ILocation
import com.example.weather.ui.location.LocationManager
import com.example.weather.ui.startup.viewmodel.StartUpViewModel
import com.example.weather.ui.startup.viewmodel.StartUpViewModelFactory
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
import kotlinx.coroutines.launch
import java.util.Locale

class StartUpFragment : Fragment(), ILocation {
    private val TAG = "StartUpFragment"
    private val PERMISSION_ID = 2005
    private lateinit var binding: FragmentStartUpBinding
    private lateinit var googleMap: GoogleMap
    private var latitudeDouble: Double = 0.0
    private var longitudeDouble: Double = 0.0
    lateinit var geocoder: Geocoder
    private var isLocationSelectedFromMap = false
    private var isClearing = false
    private lateinit var startUpViewModel: StartUpViewModel
    private lateinit var locationManager: LocationManager
    private var isMapSelected = false
    private var isGPSSelected = false
    private var locOrMap =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStartUpBinding.inflate(inflater, container, false)

        val viewModelFactory = StartUpViewModelFactory(MyApplication.getRepository())
        startUpViewModel =
            ViewModelProvider(this, viewModelFactory).get(StartUpViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initUI()
        checkForMapOrGPS()
        locationManager.checkPermissionsAndLocation()

        initMapFrag()
        activateSaveButton()
        activateCurrentImgLocation()
        activateSearchIcon()
        clearET()
        enableEditText()
    }

    private fun enableEditText() {
        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                if (NetworkManager.isInternetConnected()) {
                    binding.etSearchMap.isEnabled = true
                    binding.cancelSearch.isEnabled = true
                } else {
                    binding.etSearchMap.isEnabled = false
                    binding.cancelSearch.isEnabled = false
                }
                delay(250)
            }
        }
    }

    private fun checkForMapOrGPS() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        binding.btnMap.setOnClickListener {
            binding.btnMap.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_color
                )
            )
            binding.gpsBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            showMap(mapFragment.requireView(), true)
            isMapSelected = true
            isGPSSelected = false
            locOrMap="MAP"
        }
        binding.gpsBtn.setOnClickListener {
            binding.gpsBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.button_color
                )
            )
            binding.btnMap.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            locationManager.checkPermissionsAndLocation()
            showMap(mapFragment.requireView(), false)
            isGPSSelected = true
            isMapSelected = false
            locOrMap="Location"

        }
    }

    private fun initUI() {
        locationManager = LocationManager(this, requireContext())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        val animator = ObjectAnimator.ofFloat(binding.imgCurrentLocation, "rotation", 0f, 360f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = 2000
        animator.start()
    }

    private fun showMap(mapView: View, show: Boolean) {
        mapView.visibility = if (show) View.VISIBLE else View.GONE
        binding.etSearchMap.visibility = if (show) View.VISIBLE else View.GONE
        binding.imgCurrentLocation.visibility = if (show) View.VISIBLE else View.GONE
        binding.searchLocation.visibility = if (show) View.VISIBLE else View.GONE
        binding.cancelSearch.visibility = if (show) View.VISIBLE else View.GONE
        if(show){
            val layoutParams = binding.parentCardView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET // Remove bottom constraint
            binding.parentCardView.layoutParams = layoutParams
        }else{

            val layoutParams = binding.parentCardView.layoutParams as ConstraintLayout.LayoutParams

            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID // Remove bottom constraint
            binding.parentCardView.layoutParams = layoutParams
        }

    }

    private fun initMapFrag() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            mapFragment.view?.visibility = View.GONE
            binding.etSearchMap.visibility = View.GONE
            binding.imgCurrentLocation.visibility = View.GONE
            binding.cancelSearch.visibility = View.GONE
            binding.searchLocation.visibility = View.GONE

            googleMap.setOnMapClickListener { latLng ->
                if (NetworkManager.isInternetConnected()) {
                    Log.i(TAG, "initMapFrag: SelectedLocationLat ${latLng.latitude}")
                    setUpMapOnClick(latLng)
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.internet_connection,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun setUpMapOnClick(latLng: LatLng) {
        isLocationSelectedFromMap = true
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude
        Log.i(TAG, "setUpMapOnClick: ${latitudeDouble}--${longitudeDouble}")
        moveMap(latLng)
        convertLatLonToAddressAndSetSearchText(latLng)
    }

    override fun mapRequestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: PermissionGranted")
            }
            Log.i(TAG, "onRequestPermissionsResult: permission denied")
        }
    }

    override fun mapLocationResult(locationResult: LocationResult) {
        val lastLocation: Location = locationResult.lastLocation!!
        latitudeDouble = lastLocation.latitude
        longitudeDouble = lastLocation.longitude
        Log.i(TAG, "mapLocationResult: ${latitudeDouble}---${longitudeDouble}")
        if (isGPSSelected) {
            Log.i(TAG, "mapLocationResult: GPS SELECTED")
            savePrefAndGoToHome()
        } else if (isMapSelected) {
            Log.i(TAG, "mapLocationResult: MAP SELECTED")
            isLocationSelectedFromMap = true
            val latLng = LatLng(latitudeDouble, longitudeDouble)
            moveMap(latLng)
            convertLatLonToAddressAndSetSearchText(latLng)
        }
    }

    @SuppressLint("MissingPermission")
    fun setUpMapUsingLocationString(locationName: String) {
        val addressess = geocoder.getFromLocationName(locationName, 1)
        if (addressess != null && addressess.isNotEmpty()) {
            Log.i(TAG, "setUpMapUsingLocationString: $addressess")
            val latLng = LatLng(addressess[0].latitude, addressess[0].longitude)
            moveMap(latLng)
        }
        Log.i(TAG, "setUpMapUsingLocationString: addressess is Null")
    }

    private fun activateSaveButton() {
        binding.saveBtn.setOnClickListener {
            if (NetworkManager.isInternetConnected()) {
                Log.i(TAG, "activateSaveButton: connected")
                if (
                    (isGPSSelected || isLocationSelectedFromMap)
                    ) {
                    if (isGPSSelected) {
                        binding.saveBtn.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.text_color
                            )
                        )
                        locationManager.getLocation()
                    } else {
                        if (isLocationSelectedFromMap) {
                            binding.saveBtn.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.text_color
                                )
                            )
                            isLocationSelectedFromMap = false
                            savePrefAndGoToHome()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.init, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), R.string.internet_connection, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun savePrefAndGoToHome() {
        startUpViewModel.putBooleanInSharedPref(CONSTANTS.isNotFirstTime, true)
        startUpViewModel.putStringInSharedPref(
            CONSTANTS.sharedPreferencesKeyTempUnit,
            CONSTANTS.celsius
        )
        startUpViewModel.putStringInSharedPref(
            CONSTANTS.sharedPreferencesKeyWindSpeedUnit,
            CONSTANTS.mps
        )
        startUpViewModel.putStringInSharedPref(CONSTANTS.mapOrLoc,locOrMap)

        val isDark =
            (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (isDark) {
            startUpViewModel.setIsNightMode()
        } else {
            Log.i(TAG, "savePrefAndGoToHome: ItsLightMode")
        }
        startUpViewModel.putStringInSharedPref(
            CONSTANTS.sharedPreferencesKeyLanguage,
            CONSTANTS.english
        )
        startUpViewModel.putStringInSharedPref(
            CONSTANTS.sharedPreferencesKeyLatitude,
            latitudeDouble.toString()
        )
        startUpViewModel.putStringInSharedPref(
            CONSTANTS.sharedPreferencesKeyLongitude,
            longitudeDouble.toString()
        )
        val action =
            StartUpFragmentDirections.actionInitPrefFragmentToNavHome()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.mobile_navigation, true)
            .build()
        findNavController().navigate(action, navOptions)
    }

    private fun activateCurrentImgLocation() {
        binding.imgCurrentLocation.setOnClickListener {
            if (NetworkManager.isInternetConnected()) {
                Log.i(TAG, "activateCurrentImgLocation: Connected")
                if (locationManager.isLocationEnabled()) {
                    Log.i(TAG, "activateCurrentImgLocation: GetCurrentLocation")
                    locationManager.getLocation()
                } else {
                    Toast.makeText(requireContext(), R.string.turn_location, Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                    Log.i(TAG, "activateCurrentImgLocation:require permission")
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.internet_connection,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun activateSearchIcon() {
        binding.searchLocation.setOnClickListener {
            if (NetworkManager.isInternetConnected()) {
                setUpMapUsingLocationString(binding.etSearchMap.text.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.internet_connection
                    , Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun clearET() {
        binding.cancelSearch.setOnClickListener {
            isClearing = true
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
        }
    }

    private fun moveMap(latLng: LatLng) {
        Log.i(TAG, "moveMap: $latLng")
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude
        val markerOptions = MarkerOptions().position(latLng)
        googleMap.clear()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f), 1500, null)
        googleMap.addMarker(markerOptions)
    }

    private fun convertLatLonToAddressAndSetSearchText(latLng: LatLng) {
        latitudeDouble = latLng.latitude
        longitudeDouble = latLng.longitude
        Log.i(
            TAG,
            "convertLatLonToAddressAndSetSearchText: ${latLng.latitude} -- ${latLng.longitude}"
        )
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0].getAddressLine(0)
            binding.etSearchMap.text = Editable.Factory.getInstance().newEditable(address)
            Log.i(TAG, "convertLatLonToAddressAndSetSearchText: $address")
        }

    }

    override fun onPause() {
        super.onPause()
        binding.etSearchMap.text = Editable.Factory.getInstance().newEditable("")
    }

}