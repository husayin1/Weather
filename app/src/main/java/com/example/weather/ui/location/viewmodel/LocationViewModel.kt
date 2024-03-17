package com.example.weather.ui.location.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.IRepository
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.utilites.CONSTANTS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: IRepository) : ViewModel() {

    private val _weatherData:MutableStateFlow<RetrofitStateWeather<WeatherResponse>> = MutableStateFlow(RetrofitStateWeather.Loading)
    val weatherData : StateFlow<RetrofitStateWeather<WeatherResponse>> =  _weatherData


    fun getLocationWeatherData(
        lat: Double,
        lng: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var data = repository.getCurrentWeatherData(lat, lng, CONSTANTS.english)
            data.catch {
                _weatherData.value = RetrofitStateWeather.OnFail(it)
                Log.i("TAG", "failure: ${it.message}")
            }.collectLatest {
                Log.i("TAG", "insertFavoriteLocation:Inserted ")
                _weatherData.value = RetrofitStateWeather.OnSuccess(it)
                Log.i("TAG", "insertFavoriteLocation: ${it.addressName}---${it.lon}---${it.lat}--${it.current?.temp}")
            }
        }
    }

    fun putStringInSharedPref(key: String, value: String) {
        repository.putStringInSharedPref(key, value)
    }

    fun getStringFromSharedPref(key: String, default: String): String {
        return repository.getStringFromSharedPref(key, default)
    }

    fun insertToFavorite(favLocation: SavedLocations) {
        viewModelScope.launch {
            Log.i("TAG", "insertToFavorite: Insert from this11")
            repository.saveLocation(favLocation)
        }
    }

}

class LocationViewModelFactory(private val _repo: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            LocationViewModel(_repo) as T
        } else {
            throw java.lang.IllegalArgumentException("MapViewModel class not found")
        }
    }
}