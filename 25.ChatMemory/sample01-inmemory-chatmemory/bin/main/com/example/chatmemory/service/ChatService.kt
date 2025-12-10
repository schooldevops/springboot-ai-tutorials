package com.example.chatmemory.service

import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service

@Service
class ChatService(private val chatModel: ChatModel, private val chatMemory: ChatMemory) {

    fun chat(conversationId: String, userMessage: String): String {
        // 1. 이전 대화 기록 가져오기
        val history = chatMemory.get(conversationId, 10)

        // 2. 새 사용자 메시지 추가
        val newUserMessage = UserMessage(userMessage)
        val messages = history + newUserMessage

        // 3. AI 응답 생성
        val response = chatModel.call(Prompt(messages))
        val assistantMessage = response.result.output

        // 4. 대화 기록에 저장
        chatMemory.add(conversationId, listOf(newUserMessage, assistantMessage))

        return assistantMessage.content
    }

    fun getHistory(conversationId: String): List<Map<String, String>> {
        return chatMemory.get(conversationId, 100).map {
            mapOf("role" to it.messageType.value, "content" to it.text)
        }
    }

    fun clearHistory(conversationId: String) {
        chatMemory.clear(conversationId)
    }
}
