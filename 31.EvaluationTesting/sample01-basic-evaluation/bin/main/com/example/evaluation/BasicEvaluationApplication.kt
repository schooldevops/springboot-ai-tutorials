package com.example.evaluation

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class BasicEvaluationApplication

fun main(args: Array<String>) {
    runApplication<BasicEvaluationApplication>(*args)
}
