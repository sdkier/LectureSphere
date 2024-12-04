package com.lecturesphere.llmsummary.api.models

data class Summary(
    val sessionId: String,
    val summary: String,
    val createdAt: Long,
    val courseId: String
)