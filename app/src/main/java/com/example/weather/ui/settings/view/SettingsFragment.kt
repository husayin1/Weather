package com.example.weather.ui.settings.view

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentSettingsBinding
import com.example.weather.ui.location.ILocation
import com.example.weather.ui.location.LocationManager
import com.example.weather.ui.settings.viewmodel.SettingsViewModel
import com.example.weather.ui.settings.viewmodel.SettingsViewModelFactory
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.NetworkManager
import com.google.android.gms.location.LocationResult
import java.util.Locale

class SettingsFragment : Fragment(),ILocation {
    private val TAG = "SettingsFragment"
    private lateinit var binding:FragmentSettingsBinding
    private val PERMISSION = 7007
    private var latitude:Double=0.0
    private var longitude:Double=0.0
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var locationManager: LocationManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModelFactory =
            SettingsViewModelFactory(MyApplication.getRepository())
        settingsViewModel=ViewModelProvider(this,settingsViewModelFactory).get(SettingsViewModel::class.java)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFrag()
        activateLanguageCardView()
        activateTempCardView()
        activateWindSpeedCardView()
        activateLocationCardView()
        activateThemeCardView()
        activateLocationButtons()

    }

    private fun initFrag(){
        locationManager = LocationManager(this,requireContext())
        latitude=settingsViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLatitude,"").toDouble()
        longitude=settingsViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLongitude,"").toDouble()

        if(settingsViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,"").equals(CONSTANTS.english)){
            binding.englishButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.move))
        }else{
            binding.arabicButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }
        if(settingsViewModel.getStringFromSharedPref(CONSTANTS.mapOrLoc,"").equals("MAP")){
            binding.mapButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.move))
        }else{
            binding.locationButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }
        if(settingsViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit,"").equals(CONSTANTS.celsius)){
            binding.celsiusButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }else if(settingsViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit,"").equals(CONSTANTS.kelvin)){
            binding.kelvinButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }else{
            binding.fahrenheitButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }

        if(settingsViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyWindSpeedUnit,"").equals(CONSTANTS.mps)){
            binding.mpsButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }else{
            binding.mphButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }

        if(settingsViewModel.isDark()){
            binding.darkButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }else{
            binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
        }
    }
    override fun mapRequestPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSION){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "onRequestPermissionsResult: GRANTED PERMISSION FROM SETTINGS")
            }
        }
    }

    override fun mapLocationResult(locationResult: LocationResult) {
        val mLastLocation: Location = locationResult.lastLocation!!
        latitude=mLastLocation.latitude
        longitude=mLastLocation.longitude

        settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLatitude,latitude.toString())
        settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLongitude,longitude.toString())

    }
    private fun activateLanguageCardView(){
        binding.englishButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,CONSTANTS.english)
            binding.englishButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.arabicButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
            changeLanguage(CONSTANTS.english)
        }
        binding.arabicButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,CONSTANTS.arabic)
            binding.englishButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
            binding.arabicButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            changeLanguage(CONSTANTS.arabic)
        }
    }
    private fun activateTempCardView(){
        binding.celsiusButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit,CONSTANTS.celsius)
            binding.celsiusButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.kelvinButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
            binding.fahrenheitButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
        }
        binding.kelvinButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit,CONSTANTS.kelvin)
            binding.kelvinButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.celsiusButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
            binding.fahrenheitButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
        }
        binding.fahrenheitButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit,CONSTANTS.fahrenheit)
            binding.fahrenheitButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.kelvinButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
            binding.celsiusButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
        }
    }
    private fun activateWindSpeedCardView(){
        binding.mphButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyWindSpeedUnit,CONSTANTS.mph)
            binding.mphButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.mpsButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
        }
        binding.mpsButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyWindSpeedUnit,CONSTANTS.mps)
            binding.mpsButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.mphButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))

        }
    }
    private fun activateLocationButtons(){
        binding.locationButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.mapOrLoc,"Location")
            binding.mphButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.mpsButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))
        }
        binding.mpsButton.setOnClickListener {
            settingsViewModel.putStringInSharedPref(CONSTANTS.mapOrLoc,"MAP")
            binding.mpsButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.move))
            binding.mphButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.button))

        }
    }
    private fun activateLocationCardView(){
        binding.locationButton.setOnClickListener {
            if(NetworkManager.isInternetConnected()){
                locationManager.getLocation()
            }else{
                Toast.makeText(requireContext(),R.string.internet_connection,Toast.LENGTH_SHORT).show()
            }
        }
        binding.mapButton.setOnClickListener {
            if(NetworkManager.isInternetConnected()){
                findNavController().navigate(SettingsFragmentDirections.actionNavSettingsToMapFragment(isFromSettings = true))
            }else{
                Toast.makeText(requireContext(),R.string.internet_connection,Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun activateThemeCardView() {
        binding.lightButton.setOnClickListener {
            settingsViewModel.putBooleanInSharedPref(CONSTANTS.Theme, true)
            settingsViewModel.putBooleanInSharedPref(CONSTANTS.setIsNightMode, false)
            binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.move))
            binding.darkButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        binding.darkButton.setOnClickListener {
            settingsViewModel.putBooleanInSharedPref(CONSTANTS.Theme, true)
            settingsViewModel.putBooleanInSharedPref(CONSTANTS.setIsNightMode, true)
            binding.darkButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.move))
            binding.lightButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
    private fun changeLanguage(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context?.resources
        val configuration = Configuration()
        configuration.setLocale(locale)
        resources?.updateConfiguration(configuration, resources.displayMetrics)
        ViewCompat.setLayoutDirection(requireActivity().window.decorView, if (language == CONSTANTS.arabic) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR)
        settingsViewModel.putBooleanInSharedPref(CONSTANTS.layout, true)
        activity?.recreate()
    }


}