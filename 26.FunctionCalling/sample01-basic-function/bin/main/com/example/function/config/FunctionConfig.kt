package com.example.function.config

import com.example.function.model.CalculatorRequest
import com.example.function.model.CalculatorResponse
import java.util.function.Function
import org.springframework.ai.model.tool.Tool
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FunctionConfig {

    @Bean
    @Tool(
            name = "calculator",
            description = "Perform basic arithmetic operations: add, subtract, multiply, divide"
    )
    fun calculatorFunction(): Function<CalculatorRequest, CalculatorResponse> {
        return Function { request ->
            val result =
                    when (request.operation.lowercase()) {
                        "add" -> request.a + request.b
                        "subtract" -> request.a - request.b
                        "multiply" -> request.a * request.b
                        "divide" -> if (request.b != 0.0) request.a / request.b else Double.NaN
                        else ->
                                throw IllegalArgumentException(
                                        "Unknown operation: ${request.operation}"
                                )
                    }

            CalculatorResponse(result = result, operation = request.operation)
        }
    }
}
