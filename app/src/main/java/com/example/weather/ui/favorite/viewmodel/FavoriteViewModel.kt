package com.example.weather.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.IRepository
import com.example.weather.model.response.SavedLocations
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.NetworkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(private val _repo: IRepository) : ViewModel() {

    private val TAG = "FavoriteViewModel"
    private val _weatherData:MutableStateFlow<RetrofitStateWeather<List<SavedLocations>>> = MutableStateFlow(RetrofitStateWeather.Loading)
    val weatherData :StateFlow<RetrofitStateWeather<List<SavedLocations>>> =  _weatherData

    init {
//        loadFavorites()
        getFavFromDb()
    }
    fun getAllFavLocations(): Flow<List<SavedLocations>> {
        return _repo.getAllSavedLocations()
    }
    fun deleteFavLocation(locations: SavedLocations){
        val job = viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteLocation(locations)
        }
        job.invokeOnCompletion {
            getFavFromDb()
        }
    }
    fun addLocation(locations: SavedLocations) {
        /*val job =*/viewModelScope.launch(Dispatchers.IO) {
            _repo.saveLocation(locations)
        }
        /*job.invokeOnComp  letion {
            loadFavorites()
        }*/
    }
    fun putStringInSharedPref(key:String,value:String){
        _repo.putStringInSharedPref(key,value)
    }

    fun getFavFromDb(){
        viewModelScope.launch(Dispatchers.IO) {
            _repo.getAllSavedLocations()
                .catch {
                    RetrofitStateWeather.OnFail(it)
                }.collectLatest {
                    if(NetworkManager.isInternetConnected()){
                        var updatedList = mutableListOf<SavedLocations>()
                        it.forEach{currentFromRepo->
                            val data = if(_repo.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,CONSTANTS.english)==CONSTANTS.arabic){
                                _repo.getCurrentWeatherData(currentFromRepo.latitude,currentFromRepo.longitude,CONSTANTS.arabic)
                            }else{
                                _repo.getCurrentWeatherData(currentFromRepo.latitude,currentFromRepo.longitude,CONSTANTS.english)
                            }
                            data.collectLatest {
                                val fav= SavedLocations(
                                    address =  currentFromRepo.address,
                                    latitude = currentFromRepo.latitude,
                                    longitude = currentFromRepo.longitude,
                                    latLngString = currentFromRepo.latLngString,
                                    currentTemp = it.current?.temp,
                                    currentDescription = it.current?.weather?.get(0)?.main?.capitalize(),
                                    lastCheckedTime = System.currentTimeMillis(),
                                    icon = it.current?.weather?.get(0)?.icon
                                )
                                updatedList.add(fav)
                            }
                        }
                        _weatherData.value=RetrofitStateWeather.OnSuccess(updatedList)
                    }else{
                        _weatherData.value=RetrofitStateWeather.OnSuccess(it)
                    }
                }
        }
    }
    /*fun loadFavorites(){

        var favFromDb = emptyList<SavedLocations>()
        val job = viewModelScope.launch (Dispatchers.IO){ 
            favFromDb = repository.getAllSavedLocations()
        }
        
        job.invokeOnCompletion { 
            if(favFromDb.isNotEmpty()){
                if(NetworkManager.isInternetConnected()){
                    var updatedList = mutableListOf<SavedLocations>()
                    viewModelScope.launch { 
                        favFromDb.forEach{
                            currentFromRepo->
                            val data = if(repository.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,"")==CONSTANTS.arabic){
                                repository.getCurrentWeatherData(currentFromRepo.latitude,currentFromRepo.longitude,CONSTANTS.arabic)
                            }else{
                                repository.getCurrentWeatherData(currentFromRepo.latitude,currentFromRepo.longitude,CONSTANTS.english)
                            }
                            data.catch { 
                                retrofitStateFavorites.value=RetrofitStateFavorites.OnFail(it)
                                Log.i(TAG, "loadFavorites: ${it.message}")
                            }.collectLatest { 
                                val fav= SavedLocations(
                                    address =  currentFromRepo.address,
                                    latitude = currentFromRepo.latitude,
                                    longitude = currentFromRepo.longitude,
                                    latLngString = currentFromRepo.latLngString,
                                    currentTemp = it.current?.temp,
                                    currentDescription = it.current?.weather?.get(0)?.main?.capitalize(),
                                    lastCheckedTime = System.currentTimeMillis(),
                                    icon = it.current?.weather?.get(0)?.icon
                                )
                                updatedList.add(fav)
                            }
                            retrofitStateFavorites.value=RetrofitStateFavorites.OnSuccess(updatedList)
                            for(i in updatedList.indices){
                                Log.i(TAG, "loadFavorites: ${i} ${it}")
                            }
                        }
                    }
                }else{
                    retrofitStateFavorites.value=RetrofitStateFavorites.OnSuccess(favFromDb)
                    Log.i(TAG, "loadFavorites: NetworkConnection")
                }
            }else{
                retrofitStateFavorites.value=RetrofitStateFavorites.OnSuccess(favFromDb)
            }
        }
    }
    */

}

class FavoritesViewModelFactory (private val repository: IRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("FavoriteViewModel class not found")
        }
    }
}