package com.example.image

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class OpenAIImageApplication

fun main(args: Array<String>) {
    runApplication<OpenAIImageApplication>(*args)
}
