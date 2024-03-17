package com.example.weather.ui.alerts.videwmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather.model.repository.Repository
import com.example.weather.model.response.AlertPojo
import kotlinx.coroutines.launch

class AlertsViewModel(private val _repo: Repository):ViewModel() {

    fun insertAlert(alert:AlertPojo){
        viewModelScope.launch {
            _repo.insertAlert(alert)
        }
    }
    fun getAllAlerts():LiveData<List<AlertPojo>>{
        return _repo.getAllAlerts()
    }
    suspend fun deleteAlert(alert:AlertPojo){
        _repo.deleteAlert(alert)
    }
}

class AlertsViewModelFactory (private val _repo: Repository):ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass:Class<T>):T{
        return if (modelClass.isAssignableFrom(AlertsViewModel::class.java)){
            AlertsViewModel(_repo) as T
        }else{
            throw java.lang.IllegalArgumentException("AlertsViewModel class not found")
        }
    }
}