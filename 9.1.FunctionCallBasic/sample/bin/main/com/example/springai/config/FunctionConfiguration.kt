package com.example.springai.config

import com.example.springai.functions.CalculatorRequest
import com.example.springai.functions.CalculatorResponse
import com.example.springai.functions.TimeRequest
import com.example.springai.functions.TimeResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.function.Function

/**
 * Function Calling을 위한 함수 정의 Configuration
 * 
 * Spring AI 1.0.0-M6에서는 함수를 java.util.function.Function으로 정의하고
 * @Bean과 @Description 어노테이션을 사용하여 등록합니다.
 */
@Configuration
class FunctionConfiguration {
    
    /**
     * 계산기 함수
     * 두 숫자에 대해 사칙연산(덧셈, 뺄셈, 곱셈, 나눗셈)을 수행합니다.
     * 
     * Bean 이름이 함수 이름으로 사용됩니다 (calculatorFunction).
     */
    @Bean
    @Description("두 숫자에 대해 사칙연산(덧셈, 뺄셈, 곱셈, 나눗셈)을 수행합니다. operation은 'add', 'subtract', 'multiply', 'divide' 중 하나여야 합니다.")
    fun calculatorFunction(): Function<CalculatorRequest, CalculatorResponse> {
        return Function { request ->
            val result = when (request.operation.lowercase()) {
                "add", "+" -> request.a + request.b
                "subtract", "-" -> request.a - request.b
                "multiply", "*" -> request.a * request.b
                "divide", "/" -> {
                    if (request.b == 0.0) {
                        throw IllegalArgumentException("Division by zero is not allowed")
                    }
                    request.a / request.b
                }
                else -> throw IllegalArgumentException("Unknown operation: ${request.operation}. Supported operations: add, subtract, multiply, divide")
            }
            CalculatorResponse(result)
        }
    }
    
    /**
     * 현재 시간 조회 함수
     * timezone이 제공되면 해당 시간대로, 없으면 시스템 기본 시간대를 사용합니다.
     * 
     * Bean 이름이 함수 이름으로 사용됩니다 (getCurrentTimeFunction).
     */
    @Bean
    @Description("현재 시간을 반환합니다. timezone이 제공되면 해당 시간대로, 없으면 시스템 기본 시간대를 사용합니다.")
    fun getCurrentTimeFunction(): Function<TimeRequest, TimeResponse> {
        return Function { request ->
            val zoneId = request.timezone?.let { 
                try {
                    ZoneId.of(it)
                } catch (e: Exception) {
                    // 잘못된 시간대인 경우 시스템 기본값 사용
                    ZoneId.systemDefault()
                }
            } ?: ZoneId.systemDefault()
            
            val now = LocalDateTime.now(zoneId)
            val formatted = now.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            )
            
            TimeResponse(
                time = formatted,
                timezone = zoneId.id
            )
        }
    }
}

