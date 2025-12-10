package com.example.chatmemory.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ChatServiceTest {

    @Autowired lateinit var chatService: ChatService

    @Test
    fun `should remember conversation context`() {
        val convId = "test-conv-1"

        // First message
        chatService.chat(convId, "My name is John")

        // Second message - should remember the name
        val response = chatService.chat(convId, "What is my name?")

        assertThat(response.lowercase()).contains("john")
    }

    @Test
    fun `should retrieve conversation history`() {
        val convId = "test-conv-2"

        chatService.chat(convId, "Hello")
        chatService.chat(convId, "How are you?")

        val history = chatService.getHistory(convId)

        assertThat(history).hasSizeGreaterThanOrEqualTo(4) // 2 user + 2 assistant
    }

    @Test
    fun `should clear conversation history`() {
        val convId = "test-conv-3"

        chatService.chat(convId, "Test message")
        chatService.clearHistory(convId)

        val history = chatService.getHistory(convId)
        assertThat(history).isEmpty()
    }
}
