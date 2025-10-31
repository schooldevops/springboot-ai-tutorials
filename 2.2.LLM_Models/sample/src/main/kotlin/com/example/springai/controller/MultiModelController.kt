package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*

/**
 * 여러 LLM 모델을 동시에 사용하는 예제
 * 
 * 사용 방법:
 * 1. build.gradle.kts에서 여러 LLM Starter 의존성 추가
 * 2. application.yml에서 각 모델 설정
 * 3. @Qualifier로 특정 모델 선택
 */
@RestController
@RequestMapping("/api/multi")
class MultiModelController(
    private val primaryChatModel: ChatModel,  // 기본 모델 (@Primary)
    @Qualifier("ollamaChatModel")
    private val ollamaChatModel: ChatModel? = null  // Ollama 모델 (선택적)
) {
    
    /**
     * 기본 모델 사용 (OpenAI)
     * GET http://localhost:8080/api/multi/default?message=안녕하세요
     */
    @GetMapping("/default")
    fun chatWithDefault(@RequestParam message: String): Map<String, Any> {
        val prompt = Prompt(UserMessage(message))
        val response = primaryChatModel.call(prompt)
        
        return mapOf(
            "model" to "default (primary)",
            "message" to (response.results.firstOrNull()?.output?.text ?: "응답 없음")
        )
    }
    
    /**
     * Ollama 모델 사용 (사용 가능한 경우)
     * GET http://localhost:8080/api/multi/ollama?message=안녕하세요
     */
    @GetMapping("/ollama")
    fun chatWithOllama(@RequestParam message: String): Map<String, Any> {
        if (ollamaChatModel == null) {
            return mapOf(
                "error" to "Ollama is not configured. Please uncomment ollama dependency in build.gradle.kts"
            )
        }
        
        val prompt = Prompt(UserMessage(message))
        val response = ollamaChatModel.call(prompt)
        
        return mapOf(
            "model" to "Ollama",
            "message" to (response.results.firstOrNull()?.output?.text ?: "응답 없음")
        )
    }
    
    /**
     * 두 모델 비교
     * GET http://localhost:8080/api/multi/compare?message=Spring AI에 대해 설명해주세요
     */
    @GetMapping("/compare")
    fun compareModels(@RequestParam message: String): Map<String, Any> {
        val prompt = Prompt(UserMessage(message))
        
        val defaultResponse = primaryChatModel.call(prompt)
        val defaultText = defaultResponse.results.firstOrNull()?.output?.text ?: "응답 없음"
        
        val result = mutableMapOf<String, Any>(
            "default" to mapOf(
                "model" to "Primary",
                "message" to defaultText
            )
        )
        
        if (ollamaChatModel != null) {
            val ollamaResponse = ollamaChatModel.call(prompt)
            val ollamaText = ollamaResponse.results.firstOrNull()?.output?.text ?: "응답 없음"
            
            result["ollama"] = mapOf(
                "model" to "Ollama",
                "message" to ollamaText
            )
        } else {
            result["ollama"] = mapOf(
                "error" to "Ollama not configured"
            )
        }
        
        return result
    }
}

