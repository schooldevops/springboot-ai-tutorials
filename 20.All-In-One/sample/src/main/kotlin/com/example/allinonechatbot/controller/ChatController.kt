package com.example.allinonechatbot.controller

import com.example.allinonechatbot.dto.ChatRequest
import com.example.allinonechatbot.dto.ChatResponse
import com.example.allinonechatbot.service.AllInOneChatService
import com.example.allinonechatbot.service.ChatHistoryService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val allInOneChatService: AllInOneChatService,
    private val chatHistoryService: ChatHistoryService
) {

    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val response = allInOneChatService.chat(request.sessionId, request.message)
        
        return ChatResponse(
            message = response,
            sessionId = request.sessionId,
            source = "integrated"
        )
    }
    
    @DeleteMapping("/{sessionId}")
    fun clearHistory(@PathVariable sessionId: String): Map<String, String> {
        chatHistoryService.clearHistory(sessionId)
        return mapOf("status" to "success", "message" to "대화 기록이 삭제되었습니다")
    }
    
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "All-In-One Chatbot",
            "features" to "RAG + Function Calling + Chat Memory"
        )
    }
}
