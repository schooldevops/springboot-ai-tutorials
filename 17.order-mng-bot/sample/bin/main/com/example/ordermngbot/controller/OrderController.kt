package com.example.ordermngbot.controller

import com.example.ordermngbot.dto.ChatRequest
import com.example.ordermngbot.dto.ChatResponse
import com.example.ordermngbot.dto.Order
import com.example.ordermngbot.service.OrderService
import jakarta.annotation.PostConstruct
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val chatModel: ChatModel,
    private val orderService: OrderService
) {
    
    private val conversationHistory = mutableListOf<Message>()
    
    @PostConstruct
    fun initSampleData() {
        orderService.addOrder(Order("12345", "배송 중", "서울시 강남구 테헤란로 123", listOf("노트북", "마우스")))
        orderService.addOrder(Order("67890", "배송 준비 중", "부산시 해운대구 해운대로 456", listOf("키보드")))
        orderService.addOrder(Order("11111", "주문 완료", "제주시 제주대로 789", listOf("모니터")))
    }
    
    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        conversationHistory.add(UserMessage(request.message))
        
        val options = OllamaOptions.builder()
            .withFunction("getOrderStatus")
            .withFunction("changeDeliveryAddress")
            .withFunction("cancelOrder")
            .build()
        
        val prompt = Prompt(conversationHistory, options)
        val response = chatModel.call(prompt)
        
        conversationHistory.add(response.result.output)
        
        return ChatResponse(
            message = response.result.output.content
        )
    }
    
    @PostMapping("/reset")
    fun resetConversation(): Map<String, String> {
        conversationHistory.clear()
        return mapOf("status" to "success", "message" to "대화 이력이 초기화되었습니다")
    }
    
    @GetMapping("/history")
    fun getHistory(): List<String> {
        return conversationHistory.map { it.content }
    }
    
    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "service" to "Order Management Bot",
            "conversationTurns" to conversationHistory.size
        )
    }
}
