package com.example.lecturesphere

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import kotlin.time.Duration.Companion.seconds

class NotesGenerator(private val apiKey: String) {
    private val openAI = OpenAI(
        token = apiKey,
        timeout = Timeout(socket = 60.seconds)
    )

    suspend fun generateNotes(messages: List<Map<String, Any>>): String {
        // Format chat messages for OpenAI
        val formattedMessages = messages.map { msg ->
            val content = """
               Sender: ${msg["userName"]}
               Role: ${msg["userRole"]}
               Message: ${msg["message"]}
           """.trimIndent()

            ChatMessage(
                role = ChatRole.User,
                content = content
            )
        }

        // Add system prompt
        val systemPrompt = ChatMessage(
            role = ChatRole.System,
            content = """
               You are an expert at creating concise, well-organized study notes.
               Convert the following class discussion into clear, structured notes.
               Focus on key concepts, definitions, and important points.
               Use proper formatting with headers, bullet points, and sections.
               Ignore any off-topic conversations.
           """.trimIndent()
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(systemPrompt) + formattedMessages
        )

        val completion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices.first().message?.content ?: "Unable to generate notes"
    }
}