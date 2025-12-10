package com.example.chatmemory.controller

import com.example.chatmemory.service.ChatService
import org.springframework.web.bind.annotation.*

/** Sample 01: In-Memory Chat Memory */
@RestController
@RequestMapping("/api/chat")
class ChatMemoryController(private val chatService: ChatService) {

    data class ChatRequest(val conversationId: String, val message: String)

    @PostMapping("/message")
    fun sendMessage(@RequestBody request: ChatRequest): Map<String, String> {
        val response = chatService.chat(request.conversationId, request.message)
        return mapOf("conversationId" to request.conversationId, "response" to response)
    }

    @GetMapping("/history/{conversationId}")
    fun getHistory(@PathVariable conversationId: String): Map<String, Any> {
        val history = chatService.getHistory(conversationId)
        return mapOf("conversationId" to conversationId, "history" to history)
    }

    @DeleteMapping("/history/{conversationId}")
    fun clearHistory(@PathVariable conversationId: String): Map<String, String> {
        chatService.clearHistory(conversationId)
        return mapOf("message" to "History cleared for $conversationId")
    }
}
