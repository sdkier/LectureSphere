package com.lecturesphere.llmsummary.api

import com.lecturesphere.llmsummary.api.models.ChatSession
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserContextService {
    @GET("api/user-context/chats/{userId}")
    suspend fun getUserChats(@Path("userId") userId: String): Response<List<ChatSession>>
}