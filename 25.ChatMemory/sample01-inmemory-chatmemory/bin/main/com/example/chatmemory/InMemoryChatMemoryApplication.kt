package com.example.chatmemory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class InMemoryChatMemoryApplication

fun main(args: Array<String>) {
    runApplication<InMemoryChatMemoryApplication>(*args)
}
