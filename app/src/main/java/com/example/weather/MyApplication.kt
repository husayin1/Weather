package com.example.weather

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.weather.db.LocalDataSource
import com.example.weather.model.repository.Repository
import com.example.weather.network.RemoteDataSource
import com.example.weather.utilites.NetworkManager

class MyApplication: Application() {
    companion object{
        private var local:LocalDataSource?=null
        private var remote:RemoteDataSource?=null
        private var sharedPreferences:SharedPreferences?=null

        private val repo by lazy{
            Repository(remote!!, sharedPreferences!!, local!!)
        }
        @Synchronized
        fun getRepository(): Repository {
            if(repo==null){
                throw IllegalStateException("Repo is not init")
            }
            return repo
        }
        @Synchronized
        fun getSharedPref():SharedPreferences{
            if(sharedPreferences==null){
                throw IllegalStateException("Shared is not init")
            }
            return sharedPreferences!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        local= LocalDataSource.getInstance(this)
        remote= RemoteDataSource.getInstance()
        NetworkManager.init(this)
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this)
    }
}