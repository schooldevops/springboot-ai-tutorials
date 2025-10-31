package com.example.springai.service

import org.springframework.web.bind.annotation.*

/**
 * ModelSelectorService를 사용하는 컨트롤러
 */
@RestController
@RequestMapping("/api/smart")
class SmartChatController(
    private val modelSelectorService: ModelSelectorService
) {
    
    /**
     * 스마트 모델 선택으로 채팅
     * POST http://localhost:8080/api/smart/chat
     * Body: {"message": "안녕하세요"}
     */
    @PostMapping("/chat")
    fun smartChat(@RequestBody request: ChatRequest): Map<String, Any> {
        return modelSelectorService.smartChat(request.message)
    }
    
    /**
     * 비용 최적화 채팅
     * POST http://localhost:8080/api/smart/cost-optimized
     * Body: {"message": "안녕하세요"}
     */
    @PostMapping("/cost-optimized")
    fun costOptimizedChat(@RequestBody request: ChatRequest): Map<String, Any> {
        return modelSelectorService.costOptimizedChat(request.message)
    }
}

data class ChatRequest(
    val message: String
)

