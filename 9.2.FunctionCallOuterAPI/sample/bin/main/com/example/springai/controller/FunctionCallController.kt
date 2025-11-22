package com.example.springai.controller

import com.example.springai.service.FunctionCallService
import com.example.springai.service.MockOrderService
import com.example.springai.service.MockWeatherService
import org.springframework.web.bind.annotation.*

/**
 * 외부 API 연동 Function Calling을 테스트하기 위한 REST Controller
 */
@RestController
@RequestMapping("/api/function-call")
class FunctionCallController(
    private val functionCallService: FunctionCallService,
    private val weatherService: MockWeatherService,
    private val orderService: MockOrderService
) {
    
    /**
     * 날씨 조회 함수 테스트
     * 예: "서울 날씨 알려줘", "부산 날씨는?"
     */
    @PostMapping("/weather")
    fun weather(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required",
            "example" to "서울 날씨 알려줘"
        )
        
        val response = functionCallService.callWithWeatherFunction(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "functionUsed" to "getWeatherFunction",
            "supportedLocations" to weatherService.getSupportedLocations()
        )
    }
    
    /**
     * 주문 상태 조회 함수 테스트
     * 예: "ORD-001 주문 상태 알려줘", "ORD-002 주문 확인"
     */
    @PostMapping("/order")
    fun order(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required",
            "example" to "ORD-001 주문 상태 알려줘"
        )
        
        val response = functionCallService.callWithOrderFunction(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "functionUsed" to "getOrderStatusFunction"
        )
    }
    
    /**
     * 여러 함수를 함께 사용하는 테스트
     * LLM이 상황에 맞게 적절한 함수를 선택합니다.
     * 예: "서울 날씨 알려주고 ORD-001 주문 상태도 확인해줘"
     */
    @PostMapping("/smart")
    fun smartCall(@RequestBody request: Map<String, String>): Map<String, Any> {
        val userMessage = request["message"] ?: return mapOf(
            "error" to "Message is required",
            "example" to "서울 날씨 알려주고 ORD-001 주문 상태도 확인해줘"
        )
        
        val response = functionCallService.callWithMultipleFunctions(userMessage)
        
        return mapOf(
            "userMessage" to userMessage,
            "aiResponse" to response,
            "functionsAvailable" to listOf("getWeatherFunction", "getOrderStatusFunction")
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
     * 지원하는 도시 목록 조회
     */
    @GetMapping("/weather/locations")
    fun getSupportedLocations(): Map<String, Any> {
        return mapOf(
            "supportedLocations" to weatherService.getSupportedLocations(),
            "message" to "이 도시들의 날씨를 조회할 수 있습니다"
        )
    }
    
    /**
     * 간단한 테스트 엔드포인트
     */
    @GetMapping("/test")
    fun test(): Map<String, String> {
        return mapOf(
            "status" to "OK",
            "message" to "Function Calling with External API is running",
            "endpoints" to """
                POST /api/function-call/weather - 날씨 조회 함수 테스트
                POST /api/function-call/order - 주문 상태 조회 함수 테스트
                POST /api/function-call/smart - 여러 함수를 함께 사용
                POST /api/function-call/normal - 함수 없이 일반 호출 (비교용)
                GET /api/function-call/weather/locations - 지원하는 도시 목록
            """.trimIndent()
        )
    }
}

