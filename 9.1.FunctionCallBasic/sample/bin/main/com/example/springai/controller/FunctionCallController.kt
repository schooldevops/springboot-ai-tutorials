package com.example.springai.controller

import com.example.springai.service.FunctionCallService
import org.springframework.web.bind.annotation.*

/**
 * Function Calling을 테스트하기 위한 REST Controller
 */
@RestController
@RequestMapping("/api/function-call")
class FunctionCallController(
    private val functionCallService: FunctionCallService
) {
    
    /**
     * 계산기 함수 테스트
     * 예: "10과 5를 더해줘", "20에서 7을 빼줘"
     */
    @PostMapping("/calculator")
    fun calculator(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required",
            "example" to "10과 5를 더해줘"
        )
        
        val response = functionCallService.callWithCalculator(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "functionUsed" to "calculator"
        )
    }
    
    /**
     * 시간 조회 함수 테스트
     * 예: "현재 시간 알려줘", "서울 시간으로 현재 시간 알려줘"
     */
    @PostMapping("/time")
    fun time(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required",
            "example" to "현재 시간 알려줘"
        )
        
        val response = functionCallService.callWithTime(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "functionUsed" to "getCurrentTime"
        )
    }
    
    /**
     * 여러 함수를 함께 사용하는 테스트
     * LLM이 상황에 맞게 적절한 함수를 선택합니다.
     * 예: "10 더하기 5는?", "현재 시간은?", "계산하고 시간도 알려줘"
     */
    @PostMapping("/smart")
    fun smartCall(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required",
            "example" to "10과 5를 더하고 현재 시간도 알려줘"
        )
        
        val response = functionCallService.callWithMultipleFunctions(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "functionsAvailable" to listOf("calculator", "getCurrentTime")
        )
    }
    
    /**
     * 함수 없이 일반 호출 (비교용)
     */
    @PostMapping("/normal")
    fun normalCall(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required"
        )
        
        val response = functionCallService.callWithoutFunction(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "note" to "This is a normal call without function calling"
        )
    }
    
    /**
     * 간단한 테스트 엔드포인트
     */
    @GetMapping("/test")
    fun test(): Map<String, String> {
        return mapOf(
            "status" to "OK",
            "message" to "Function Calling API is running",
            "endpoints" to """
                POST /api/function-call/calculator - 계산기 함수 테스트
                POST /api/function-call/time - 시간 조회 함수 테스트
                POST /api/function-call/smart - 여러 함수를 함께 사용
                POST /api/function-call/normal - 함수 없이 일반 호출 (비교용)
            """.trimIndent()
        )
    }
}

