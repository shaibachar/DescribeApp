package com.example.describeapp.network

import com.example.describeapp.data.DeepSeekRequest
import com.example.describeapp.data.DeepSeekResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekApiService {
    @POST("chat/completions")
    suspend fun getImageDescription(
        @Header("Authorization") authorization: String,
        @Body request: DeepSeekRequest
    ): Response<DeepSeekResponse>
}