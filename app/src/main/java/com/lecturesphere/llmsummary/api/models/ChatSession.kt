package com.lecturesphere.llmsummary.api.models

data class ChatSession(
    val sessionId: String,
    val courseId: String,
    val messages: List<ChatMessage>
)