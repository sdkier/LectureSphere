package com.lecturesphere.llmsummary.api.models

data class ChatMessage(
    val userId: String,
    val content: String,
    val timestamp: Long
)