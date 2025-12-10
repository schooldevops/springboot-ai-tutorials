package com.example.relevancy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class RelevancyEvaluatorApplication

fun main(args: Array<String>) {
    runApplication<RelevancyEvaluatorApplication>(*args)
}
