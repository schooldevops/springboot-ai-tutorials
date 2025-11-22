package com.example.springai.functions

/**
 * 계산기 함수 요청 데이터 클래스
 */
data class CalculatorRequest(
    /**
     * 연산 종류: 'add', 'subtract', 'multiply', 'divide'
     */
    val operation: String,
    
    /**
     * 첫 번째 숫자
     */
    val a: Double,
    
    /**
     * 두 번째 숫자
     */
    val b: Double
)

