package com.example.chatclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class ChatClientBasicApplication

fun main(args: Array<String>) {
    runApplication<ChatClientBasicApplication>(*args)
}
