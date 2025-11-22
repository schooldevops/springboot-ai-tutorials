package com.example.conversationalchatbot.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.messages.UserMessage
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatHistoryServiceTest {
    
    private lateinit var chatHistoryService: ChatHistoryService
    
    @BeforeEach
    fun setup() {
        chatHistoryService = ChatHistoryService()
    }
    
    @Test
    fun `should store and retrieve chat history`() {
        // given
        val sessionId = "user-123"
        val message = UserMessage("안녕하세요")
        
        // when
        chatHistoryService.addMessage(sessionId, message)
        val history = chatHistoryService.getHistory(sessionId)
        
        // then
        assertEquals(1, history.size)
        assertEquals("안녕하세요", history[0].content)
    }
    
    @Test
    fun `should maintain separate histories for different sessions`() {
        // given
        val session1 = "user-1"
        val session2 = "user-2"
        
        // when
        chatHistoryService.addMessage(session1, UserMessage("메시지1"))
        chatHistoryService.addMessage(session2, UserMessage("메시지2"))
        
        // then
        assertEquals(1, chatHistoryService.getHistory(session1).size)
        assertEquals(1, chatHistoryService.getHistory(session2).size)
    }
    
    @Test
    fun `should clear history for specific session`() {
        // given
        val sessionId = "user-123"
        chatHistoryService.addMessage(sessionId, UserMessage("메시지"))
        
        // when
        chatHistoryService.clearHistory(sessionId)
        
        // then
        assertTrue(chatHistoryService.getHistory(sessionId).isEmpty())
    }
    
    @Test
    fun `should return empty list for non-existent session`() {
        // when
        val history = chatHistoryService.getHistory("non-existent")
        
        // then
        assertTrue(history.isEmpty())
    }
}
