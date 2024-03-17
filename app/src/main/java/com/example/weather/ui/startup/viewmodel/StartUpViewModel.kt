package com.example.weather.ui.startup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.IRepository
import com.example.weather.utilites.CONSTANTS

class StartUpViewModel(private val _repo: IRepository):ViewModel() {
    fun putStringInSharedPref(key:String,value:String){
        _repo.putStringInSharedPref(key,value)
    }
    fun putBooleanInSharedPref(key:String,value:Boolean){
        _repo.putBooleanInSharedPref(key, value)
    }
    fun setIsNightMode() {
        _repo.putBooleanInSharedPref(CONSTANTS.setIsNightMode , true)
    }

}
class StartUpViewModelFactory (private val _repo: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(StartUpViewModel::class.java)){
            StartUpViewModel(_repo) as T
        } else{
            throw java.lang.IllegalArgumentException("StartUpViewModel class not found")
        }
    }
}