package com.lecturesphere.llmsummary.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val OPENAI_API_KEY = "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"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
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

    private val userContextRetrofit = Retrofit.Builder()
        .baseUrl("http://your-usercontext-app-url/") //REPLACE
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openAiService: OpenAiService = openAiRetrofit.create(OpenAiService::class.java)
    val userContextService: UserContextService = userContextRetrofit.create(UserContextService::class.java)
}