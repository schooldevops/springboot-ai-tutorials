package com.example.allinonechatbot.service

import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.stereotype.Service

@Service
class AllInOneChatService(
    private val chatModel: ChatModel,
    private val ragService: RAGService,
    private val chatHistoryService: ChatHistoryService
) {
    
    fun chat(sessionId: String, userMessage: String): String {
        // 1. Add user message to history
        chatHistoryService.addMessage(sessionId, UserMessage(userMessage))
        
        // 2. RAG: Search similar documents
        val similarDocs = ragService.searchSimilarDocuments(userMessage, 3)
        
        // 3. Build context from documents
        val context = if (similarDocs.isNotEmpty()) {
            "다음 문서를 참고하여 답변하세요:\n" +
            similarDocs.joinToString("\n") { it.content }
        } else {
            ""
        }
        
        // 4. Get conversation history
        val history = chatHistoryService.getHistory(sessionId)
        
        // 5. Build messages
        val messages = if (context.isNotEmpty()) {
            listOf(SystemMessage(context)) + history
        } else {
            history
        }
        
        // 6. Call AI with Function Calling enabled
        val options = OllamaOptions.builder()
            .withFunction("getWeather")
            .build()
        
        val prompt = Prompt(messages, options)
        val response = chatModel.call(prompt)
        
        // 7. Save AI response to history
        chatHistoryService.addMessage(sessionId, response.result.output)
        
        return response.result.output.content
    }
}
