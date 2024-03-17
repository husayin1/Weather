package com.example.weather.ui.home.viewmodel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.IRepository
import com.example.weather.model.repository.Repository
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.CONSTANTS.sharedPreferencesKeyLanguage
import com.example.weather.utilites.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(private val _repo: IRepository) : ViewModel() {

    private val TAG = "HomeViewModel"
    private lateinit var geocoder: Geocoder
    private var longitudeDouble: Double = 0.0
    private var latitudeDouble: Double = 0.0
    val cityName = MutableLiveData<String>()

    private var retrofitStateWeather:MutableStateFlow<RetrofitStateWeather<WeatherResponse>> = MutableStateFlow(RetrofitStateWeather.Loading)
    val response = retrofitStateWeather


    fun init(context: Context) {
        geocoder = Geocoder(context, Locale.getDefault())
    }


    suspend fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_repo.getStringFromSharedPref(sharedPreferencesKeyLanguage, CONSTANTS.english)
                    .equals(CONSTANTS.english)
            ) {
                _repo.getCurrentWeatherData(
                    latitudeDouble,
                    longitudeDouble,
                    CONSTANTS.english
                ).catch {
                    retrofitStateWeather.value=RetrofitStateWeather.OnFail(it)
                    Log.i(TAG, "getCurrentWeather: ${it.message} ")
                }.collectLatest {
                    retrofitStateWeather.value=RetrofitStateWeather.OnSuccess(it)
                    Log.i(TAG, "getCurrentWeather: ${it.addressName}")
                    Log.i(TAG, "getCurrentWeather: ${it.current?.temp}")
                    Log.i(TAG, "getCurrentWeather: ${it.lon}")
                    Log.i(TAG, "getCurrentWeather: ${it.lat}")
                }
            } else {
                Log.i(TAG, "getCurrentWeather: arabic")
                _repo.getCurrentWeatherData(
                    latitudeDouble,
                    longitudeDouble,
                    CONSTANTS.arabic
                ).catch {
                    retrofitStateWeather.value =
                        RetrofitStateWeather.OnFail(it)
                    Log.i(TAG, "getCurrentWeather: ${it.message} ")
                }.collectLatest {
                    retrofitStateWeather.value = RetrofitStateWeather.OnSuccess(it)
                }
            }
        }
    }

    suspend fun getOfflineWeatherData(): WeatherResponse? {
        return _repo.getWeatherDataFromDB()
    }

    fun getAddressFromLatLng(lat: Double, lng: Double): String {
        var addressName = ""
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            addressName = "${address.locality} , ${address.adminArea ?: "unknown"} "
        }
        return addressName
    }

    suspend fun updateWeatherData(weatherResponse: WeatherResponse, address: String) {
        _repo.updateWeatherData(
            WeatherResponse(
                weatherResponse.alerts,
                weatherResponse.current,
                weatherResponse.daily,
                weatherResponse.lon,
                weatherResponse.hourly,
                weatherResponse.lon,
                weatherResponse.addressName
            )
        )
    }

    fun setLatitudeAndLongitude() {
        latitudeDouble =
            _repo.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLatitude, "0.0").toDouble()
        longitudeDouble =
            _repo.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLongitude, "0.0").toDouble()
        Log.i(TAG, "setLatitudeAndLongitude: $latitudeDouble")
        Log.i(TAG, "setLatitudeAndLongitude: $longitudeDouble")

    }

    fun updateCityName(isAdded: Boolean, addressName: String?) {
        if (isAdded) {
            if (NetworkManager.isInternetConnected()) {
                Log.i(TAG, "updateCityName: Connected")
                val addresses = geocoder.getFromLocation(latitudeDouble, longitudeDouble, 1)
                Log.i(TAG, "updateCityName: $addresses")
                if (addresses != null) {
                    Log.i(TAG, "updateCityName:${addresses}")
                    val address = addresses[0]
                    Log.i(
                        TAG,
                        "updateCityName: ${address.locality} ${address.adminArea} ${address.countryName} ${address.subAdminArea}"
                    )
                    val addressName = " ${address.adminArea}, ${address.countryName}"
                    if (addressName == null) {
                        cityName.value = "Unknown"
                    } else {
                        cityName.value = addressName
                    }
                }
            } else {
                Log.i(TAG, "updateCityName: No Connection")
                if (addressName != null) {
                    cityName.value = addressName!!
                } else {
                    cityName.value = "Need Connection"
                }
            }


        }
    }

    fun getStringFromSharedPref(key: String, stringDefault: String): String {
        return _repo.getStringFromSharedPref(key, stringDefault)
    }


}

class HomeViewModelFactory(private val _repo: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("HomeViewModel class not found")
        }
    }
}
