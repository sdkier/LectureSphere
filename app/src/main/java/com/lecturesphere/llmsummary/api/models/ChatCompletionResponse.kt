package com.lecturesphere.llmsummary.api.models

data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>
)