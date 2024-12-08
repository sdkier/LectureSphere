package com.lecturesphere.llmsummary.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val API_KEY = "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"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(newRequest)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val openAiRetrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openAiService: OpenAiService = openAiRetrofit.create(OpenAiService::class.java)
    val userContextService: UserContextService = openAiRetrofit.create(UserContextService::class.java)
}