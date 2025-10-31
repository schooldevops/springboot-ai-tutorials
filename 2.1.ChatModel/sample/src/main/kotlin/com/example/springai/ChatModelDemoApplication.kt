package com.example.springai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatModelDemoApplication

fun main(args: Array<String>) {
    runApplication<ChatModelDemoApplication>(*args)
}

