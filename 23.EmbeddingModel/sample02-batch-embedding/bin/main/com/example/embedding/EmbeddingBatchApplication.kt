package com.example.embedding

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class EmbeddingBatchApplication

fun main(args: Array<String>) {
    runApplication<EmbeddingBatchApplication>(*args)
}
