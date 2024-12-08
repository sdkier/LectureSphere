package com.lecturesphere.llmsummary.api.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "summaries")
data class Summary(
    @PrimaryKey
    val sessionId: String,
    val summary: String,
    val createdAt: Long,
    val courseId: String
)