package com.example.weather.ui.home

import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.response.WeatherResponse
import com.example.weather.model.response.repos.IRepository
import com.example.weather.model.response.repos.Repository
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.CONSTANTS.sharedPreferencesKeyLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val _repo: IRepository) : ViewModel() {

    private lateinit var geocoder: Geocoder
    private var longitudeDouble: Double = 31.15
    private var latitudeDouble: Double = 32.17
    val cityName = MutableLiveData<String>()

    val retrofitStateWeather = MutableStateFlow<RetrofitStateWeather>(RetrofitStateWeather.Loading)
    private val mutableWeather = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse> = mutableWeather



    fun getCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_repo.getStringFromSharedPref(sharedPreferencesKeyLanguage, "").equals(CONSTANTS.english)) {
                val data = _repo.getCurrentWeatherData(
                    latitudeDouble,
                    longitudeDouble,
                    CONSTANTS.english
                )
                data.catch {
                    retrofitStateWeather.value =
                        RetrofitStateWeather.OnFail(Throwable("Error retrieving data"))
                }
                    .collectLatest {
                        retrofitStateWeather.value = RetrofitStateWeather.OnSuccess(it)
                    }
            } else {

                val data = _repo.getCurrentWeatherData(
                    latitudeDouble,
                    longitudeDouble,
                    CONSTANTS.english
                )
                data.catch {
                    retrofitStateWeather.value =
                        RetrofitStateWeather.OnFail(Throwable("Error retrieving data"))
                }.collectLatest {
                    retrofitStateWeather.value = RetrofitStateWeather.OnSuccess(it)
                }
            }
        }
    }
    fun setLatitudeAndLongitude() {
        latitudeDouble =
            _repo.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLatitude, "")?.toDouble() ?: 0.0
        longitudeDouble =
            _repo.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLongitude, "")?.toDouble() ?: 0.0
    }
    fun getStringFromSharedPref(key: String, stringDefault: String): String {
        return _repo.getStringFromSharedPref(key, stringDefault)
    }



}

class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAS    T")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
