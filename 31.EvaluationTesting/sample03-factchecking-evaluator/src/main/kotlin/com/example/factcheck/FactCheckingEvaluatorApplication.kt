package com.example.factcheck

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class FactCheckingEvaluatorApplication

fun main(args: Array<String>) {
    runApplication<FactCheckingEvaluatorApplication>(*args)
}
