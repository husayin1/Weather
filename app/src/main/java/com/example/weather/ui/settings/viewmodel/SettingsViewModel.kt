package com.example.weather.ui.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.IRepository
import com.example.weather.utilites.CONSTANTS

class SettingsViewModel(private val _repo: IRepository) : ViewModel() {
    private val TAG = "SettingsViewModel"

    fun putStringInSharedPref(key:String,value:String){
        _repo.putStringInSharedPref(key,value)
    }
    fun getStringFromSharedPref(key:String,defaultValue:String):String{
        return _repo.getStringFromSharedPref(key,defaultValue)
    }

    fun putBooleanInSharedPref(key:String,value:Boolean){
        _repo.putBooleanInSharedPref(key,value)
    }
    fun isDark():Boolean{
        return _repo.getBooleanFromSharedPref(CONSTANTS.setIsNightMode,false)
    }
}

class SettingsViewModelFactory (private val _repo: IRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(_repo) as T
        } else{
            throw java.lang.IllegalArgumentException("SettingsViewModel class not found")
        }
    }
}