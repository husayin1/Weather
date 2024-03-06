package com.example.weather.network

import com.example.weather.utilites.CONSTANTS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    val retrofitInstance :ApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CONSTANTS.BASE_URL).build()
            .create(ApiService::class.java)
    }
}