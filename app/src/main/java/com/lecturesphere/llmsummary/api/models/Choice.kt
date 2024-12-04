package com.lecturesphere.llmsummary.api.models

data class Choice(
    val message: Message,
    val finish_reason: String
)