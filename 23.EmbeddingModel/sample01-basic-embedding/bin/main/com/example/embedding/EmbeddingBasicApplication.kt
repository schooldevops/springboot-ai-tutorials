package com.example.embedding

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class EmbeddingBasicApplication

fun main(args: Array<String>) {
    runApplication<EmbeddingBasicApplication>(*args)
}
