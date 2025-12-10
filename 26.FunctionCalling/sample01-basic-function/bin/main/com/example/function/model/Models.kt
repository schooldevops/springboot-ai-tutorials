package com.example.function.model

data class CalculatorRequest(val operation: String, val a: Double, val b: Double)

data class CalculatorResponse(val result: Double, val operation: String)
