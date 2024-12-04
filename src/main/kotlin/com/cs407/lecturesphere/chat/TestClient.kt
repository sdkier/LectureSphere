package com.cs407.lecturesphere.chat

fun main() {
    val wsManager = WebSocketManager.getInstance()

    wsManager.setMessageListener { message ->
        println("Received: $message")
    }

    wsManager.setConnectionListener { connected ->
        println("Connection status: $connected")
    }


    wsManager.startSocket()

    Thread.sleep(1000)

    wsManager.sendMessage("Hello from test client!")

    Thread.sleep(Long.MAX_VALUE)
}