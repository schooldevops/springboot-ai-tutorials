package com.example.embedding

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class OpenAIEmbeddingApplication

fun main(args: Array<String>) {
    runApplication<OpenAIEmbeddingApplication>(*args)
}
