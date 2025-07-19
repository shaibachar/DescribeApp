package com.example.describeapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.deepseek.com/v1/"
    
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val deepSeekApiService: DeepSeekApiService by lazy {
        retrofit.create(DeepSeekApiService::class.java)
    }
}