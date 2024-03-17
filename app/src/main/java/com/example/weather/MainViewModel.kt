package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.IRepository
import com.example.weather.utilites.CONSTANTS
import kotlinx.coroutines.launch

class MainViewModel(private val _repo: IRepository):ViewModel() {
    fun putBooleanInSharedPref(key:String,value:Boolean){
        _repo.putBooleanInSharedPref(key,value)
    }
    fun isArabic():Boolean{
        return _repo.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,CONSTANTS.english).equals(CONSTANTS.arabic)
    }
    fun isLayoutChangedBySettings(): Boolean {
        return _repo.getBooleanFromSharedPref(CONSTANTS.layout, false)
    }

    fun isThemeChangedBySettings(): Boolean {
        return _repo.getBooleanFromSharedPref(CONSTANTS.Theme,false)
    }

    fun setIsThemeChangedBySettingsToFalse() {
        _repo.putBooleanInSharedPref(CONSTANTS.Theme,false)
    }


}
class MainViewModelFactory (private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            MainViewModel(repository) as T
        } else{
            throw java.lang.IllegalArgumentException("Initial MainViewModel class not found")
        }
    }
}