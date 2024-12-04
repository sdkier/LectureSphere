package com.cs407.lecturesphere.chat

import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.TextMessage

data class Location(
    val latitude: Double,
    val longitude: Double
)

class ChatRoom(
    val id: String,
    val location: Location,
    val radius: Double = 100.0 // radius in meters
) {
    private val sessions = mutableSetOf<WebSocketSession>()

    fun addSession(session: WebSocketSession) {
        sessions.add(session)
    }

    fun removeSession(session: WebSocketSession) {
        sessions.remove(session)
    }

    fun broadcast(message: String, sender: WebSocketSession) {
        sessions.forEach { session ->
            if (session.isOpen) {
                session.sendMessage(TextMessage(message))
            }
        }
    }
}