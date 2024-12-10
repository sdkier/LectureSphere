package com.example.lecturesphere

import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebSocketManager private constructor() {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var messageListener: ((String) -> Unit)? = null
    private var connectionListener: ((Boolean) -> Unit)? = null
    private var currentClassId: String? = null

    companion object {
        private const val WS_URL = "ws://10.0.2.2:8080/chat"
        private const val NORMAL_CLOSURE_STATUS = 1000

        @Volatile
        private var instance: WebSocketManager? = null

        fun getInstance(): WebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: WebSocketManager().also { instance = it }
            }
        }
    }

    fun setMessageListener(listener: (String) -> Unit) {
        messageListener = listener
    }

    fun setConnectionListener(listener: (Boolean) -> Unit) {
        connectionListener = listener
    }

    fun connectToClass(classId: String) {
        currentClassId = classId
        startSocket()
    }

    fun startSocket() {
        val request = Request.Builder()
            .url(WS_URL)
            .build()

        webSocket = client.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            connectionListener?.invoke(true)

            // Send connection message with classId
            currentClassId?.let { classId ->
                val connectMessage = JSONObject().apply {
                    put("type", "connect")
                    put("userId", TrackedUser.userId)
                    put("classId", classId)
                }
                webSocket.send(connectMessage.toString())
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            messageListener?.invoke(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            connectionListener?.invoke(false)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            connectionListener?.invoke(false)
        }
    }

    fun sendMessage(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun reconnect() {
        closeSocket()
        startSocket()
    }

    fun closeSocket() {
        try {
            webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            webSocket = null
            connectionListener?.invoke(false)
        } catch (e: Exception) {
            connectionListener?.invoke(false)
        }
    }
}