package com.cs407.lecturesphere.chat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatApplication

fun main(args: Array<String>) {
    runApplication<ChatApplication>(*args)
}