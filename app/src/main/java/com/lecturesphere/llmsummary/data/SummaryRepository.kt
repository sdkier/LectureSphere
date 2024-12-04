package com.lecturesphere.llmsummary.data

import com.lecturesphere.llmsummary.api.NetworkClient
import com.lecturesphere.llmsummary.api.models.*
import com.lecturesphere.llmsummary.data.local.SummaryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SummaryRepository(private val summaryDao: SummaryDao) {
    private val openAiService = NetworkClient.openAiService
    private val userContextService = NetworkClient.userContextService

    suspend fun generateSummary(chatSession: ChatSession): Result<Summary> {
        return withContext(Dispatchers.IO) {
            try {
                summaryDao.getSummary(chatSession.sessionId)?.let {
                    return@withContext Result.success(it)
                }

                val prompt = generatePrompt(chatSession.messages)
                val request = ChatCompletionRequest(
                    messages = listOf(
                        Message("system", "You are a helpful assistant that summarizes student discussions."),
                        Message("user", prompt)
                    )
                )

                val response = openAiService.generateSummary(request)
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("API call failed: ${response.code()}"))
                }

                val summaryText = response.body()?.choices?.firstOrNull()?.message?.content
                    ?: return@withContext Result.failure(Exception("No summary generated"))

                val summary = Summary(
                    sessionId = chatSession.sessionId,
                    summary = summaryText,
                    createdAt = System.currentTimeMillis(),
                    courseId = chatSession.courseId
                )

                summaryDao.insertSummary(summary)
                Result.success(summary)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun generatePrompt(messages: List<ChatMessage>): String {
        val formattedChat = messages.joinToString("\n") { msg ->
            "[${formatTimestamp(msg.timestamp)}] ${msg.userId}: ${msg.content}"
        }

        return """
            Please provide a concise summary of the following student discussion. 
            Focus on the main topics, key points, and any questions or concerns raised:

            $formattedChat

            Summary:
        """.trimIndent()
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timestamp))
    }

    suspend fun getUserChats(userId: String): Result<List<ChatSession>> {
        return try {
            val response = userContextService.getUserChats(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch user chats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}