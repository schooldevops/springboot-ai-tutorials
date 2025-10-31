package com.example.springai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * 상황에 따라 적절한 모델을 선택하는 서비스 예제
 */
@Service
class ModelSelectorService(
    private val primaryChatModel: ChatModel,
    @Qualifier("ollamaChatModel")
    private val ollamaChatModel: ChatModel? = null
) {
    
    /**
     * 질문의 복잡도에 따라 모델 선택
     */
    fun selectModel(question: String): ChatModel {
        return when {
            // 간단한 질문은 Ollama (무료) - 사용 가능한 경우
            question.length < 50 && ollamaChatModel != null -> ollamaChatModel
            // 복잡한 질문은 기본 모델 (유료)
            else -> primaryChatModel
        }
    }
    
    /**
     * 적절한 모델을 자동으로 선택하여 응답
     */
    fun smartChat(message: String): Map<String, Any> {
        val model = selectModel(message)
        val modelName = if (model == ollamaChatModel) "Ollama" else "Primary (OpenAI)"
        
        val prompt = Prompt(UserMessage(message))
        val response = model.call(prompt)
        
        return mapOf(
            "selectedModel" to modelName,
            "message" to (response.results.firstOrNull()?.output?.text ?: "응답 없음"),
            "questionLength" to message.length
        )
    }
    
    /**
     * 비용 최적화를 위한 모델 선택
     */
    fun costOptimizedChat(message: String): Map<String, Any> {
        // 간단한 인사나 짧은 질문은 Ollama 사용
        val useOllama = message.length < 30 && 
                       message.lowercase().contains(Regex("안녕|hi|hello|thanks|감사")) &&
                       ollamaChatModel != null
        
        val model = if (useOllama) ollamaChatModel!! else primaryChatModel
        val modelName = if (useOllama) "Ollama (free)" else "Primary (paid)"
        
        val prompt = Prompt(UserMessage(message))
        val response = model.call(prompt)
        
        return mapOf(
            "selectedModel" to modelName,
            "message" to (response.results.firstOrNull()?.output?.text ?: "응답 없음"),
            "costOptimized" to useOllama
        )
    }
}

