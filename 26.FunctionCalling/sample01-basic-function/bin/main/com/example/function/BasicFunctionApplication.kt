package com.example.function

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class BasicFunctionApplication

fun main(args: Array<String>) {
    runApplication<BasicFunctionApplication>(*args)
}
