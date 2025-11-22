package com.example.conversationalchatbot.controller

import com.example.conversationalchatbot.dto.ChatRequest
import com.example.conversationalchatbot.dto.ChatResponse
import com.example.conversationalchatbot.service.ChatHistoryService
import com.example.conversationalchatbot.service.ConversationalChatService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val conversationalChatService: ConversationalChatService,
    private val chatHistoryService: ChatHistoryService
) {

    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val response = conversationalChatService.chat(request.sessionId, request.message)
        
        return ChatResponse(
            message = response,
            sessionId = request.sessionId
        )
    }
    
    @DeleteMapping("/{sessionId}")
    fun clearHistory(@PathVariable sessionId: String): Map<String, String> {
        chatHistoryService.clearHistory(sessionId)
        return mapOf(
            "status" to "success",
            "message" to "대화 기록이 삭제되었습니다"
        )
    }
    
    @GetMapping("/{sessionId}/history")
    fun getHistory(@PathVariable sessionId: String): Map<String, Any> {
        val history = chatHistoryService.getHistory(sessionId)
        return mapOf(
            "sessionId" to sessionId,
            "messageCount" to history.size,
            "messages" to history.map { mapOf("content" to it.content) }
        )
    }
    
    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "UP",
            "service" to "Conversational Chatbot",
            "activeSessions" to chatHistoryService.getAllSessions().size
        )
    }
}
