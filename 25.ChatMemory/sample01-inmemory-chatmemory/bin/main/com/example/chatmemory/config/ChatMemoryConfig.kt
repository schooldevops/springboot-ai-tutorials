package com.example.chatmemory.config

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatMemoryConfig {

    @Bean
    fun chatMemory(): ChatMemory {
        return InMemoryChatMemory()
    }
}
