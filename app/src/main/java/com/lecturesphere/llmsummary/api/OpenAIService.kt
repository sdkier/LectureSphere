package com.lecturesphere.llmsummary.api

import com.lecturesphere.llmsummary.api.models.ChatCompletionRequest
import com.lecturesphere.llmsummary.api.models.ChatCompletionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAiService {
    @POST("v1/chat/completions")
    suspend fun generateSummary(@Body request: ChatCompletionRequest): Response<ChatCompletionResponse>
}