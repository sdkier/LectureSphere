package com.lecturesphere.llmsummary.api.models

data class ChatCompletionRequest(
    val model: String = "gpt-4",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 500
)