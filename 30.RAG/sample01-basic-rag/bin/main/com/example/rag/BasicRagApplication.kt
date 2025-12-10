package com.example.rag

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class BasicRagApplication

fun main(args: Array<String>) {
    runApplication<BasicRagApplication>(*args)
}
