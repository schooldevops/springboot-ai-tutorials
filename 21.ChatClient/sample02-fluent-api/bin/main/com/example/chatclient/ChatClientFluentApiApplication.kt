package com.example.chatclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class ChatClientFluentApiApplication

fun main(args: Array<String>) {
    runApplication<ChatClientFluentApiApplication>(*args)
}
