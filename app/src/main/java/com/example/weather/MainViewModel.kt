package com.example.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.response.repos.IRepository

class MainViewModel(private val _repo: IRepository):ViewModel() {

    fun putBooleanInSharedPref(key:String,value:Boolean){
        _repo.putBooleanInSharedPref(key,value)
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