package com.example.annotations

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class ServerAnnotationsApplication

fun main(args: Array<String>) {
    runApplication<ServerAnnotationsApplication>(*args)
}
