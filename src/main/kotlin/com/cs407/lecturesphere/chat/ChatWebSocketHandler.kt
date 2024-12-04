package com.cs407.lecturesphere.chat

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

class ChatWebSocketHandler : TextWebSocketHandler() {
    private val sessions = ConcurrentHashMap<WebSocketSession, String>()
    private val mapper = jacksonObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session] = session.id
        println("Client connected: ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val messageData = mapper.readTree(message.payload)
            println("Received message: ${message.payload}") // Debug log

            when (messageData.get("type").asText()) {
                "connect" -> {
                    val userId = messageData.get("userId").asText()
                    sessions[session] = userId
                    println("User connected: $userId") // Debug log
                }
                "message" -> {
                    val userId = messageData.get("userId").asText()
                    val messageContent = messageData.get("message").asText()
                    val broadcastMessage = mapper.writeValueAsString(mapOf(
                        "userId" to userId,
                        "message" to messageContent
                    ))

                    println("Broadcasting message: $broadcastMessage") // Debug log

                    sessions.keys.forEach { clientSession ->
                        if (clientSession.isOpen) {
                            clientSession.sendMessage(TextMessage(broadcastMessage))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("Error handling message: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        println("Client disconnected: ${session.id}")
    }
}