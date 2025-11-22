package com.example.conversationalchatbot.service

import org.springframework.ai.chat.messages.Message
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatHistoryService {
    
    private val sessions = ConcurrentHashMap<String, MutableList<Message>>()
    
    fun addMessage(sessionId: String, message: Message) {
        sessions.computeIfAbsent(sessionId) { mutableListOf() }.add(message)
    }
    
    fun getHistory(sessionId: String): List<Message> {
        return sessions[sessionId]?.toList() ?: emptyList()
    }
    
    fun clearHistory(sessionId: String) {
        sessions.remove(sessionId)
    }
    
    fun getAllSessions(): Set<String> {
        return sessions.keys
    }
}
