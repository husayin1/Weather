package com.example.weather.ui.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather.model.repository.IRepository
import com.example.weather.utilites.CONSTANTS

class SplashViewModel(private val _repo: IRepository) : ViewModel() {
    fun isNotFirstTime(): Boolean {
        return _repo.getBooleanFromSharedPref(CONSTANTS.isNotFirstTime, false)
    }

    fun isDark(): Boolean {
        return _repo.getBooleanFromSharedPref(CONSTANTS.setIsNightMode, false)
    }
}

class SplashViewModelFactory(private val _repo: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            SplashViewModel(_repo) as T
        } else {
            throw java.lang.IllegalArgumentException("SplashViewModel class not found")
        }
    }
}