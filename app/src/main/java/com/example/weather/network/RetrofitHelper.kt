package com.example.weather.network

import com.example.weather.utilites.CONSTANTS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitHelper {
    val retrofitInstance :Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CONSTANTS.BASE_URL).build()
    }
}