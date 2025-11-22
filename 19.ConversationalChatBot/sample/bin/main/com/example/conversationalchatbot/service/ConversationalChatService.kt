package com.example.conversationalchatbot.service

import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service

@Service
class ConversationalChatService(
    private val chatModel: ChatModel,
    private val chatHistoryService: ChatHistoryService
) {
    
    fun chat(sessionId: String, userMessage: String): String {
        // 1. Add user message to history
        chatHistoryService.addMessage(sessionId, UserMessage(userMessage))
        
        // 2. Get full conversation history
        val history = chatHistoryService.getHistory(sessionId)
        
        // 3. Call AI with history
        val prompt = Prompt(history)
        val response = chatModel.call(prompt)
        
        // 4. Save AI response to history
        chatHistoryService.addMessage(sessionId, response.result.output)
        
        return response.result.output.content
    }
}
