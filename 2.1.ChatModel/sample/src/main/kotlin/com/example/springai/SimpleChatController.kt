package com.example.springai

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*

/**
 * ChatModel의 기본 사용법을 보여주는 간단한 컨트롤러
 */
@RestController
@RequestMapping("/api/simple")
class SimpleChatController(
    private val chatModel: ChatModel
) {
    
    /**
     * 가장 기본적인 ChatModel 사용 예제
     * GET http://localhost:8080/api/simple/basic
     */
    @GetMapping("/basic")
    fun basicChat(): String {
        // 1. UserMessage 생성
        val userMessage = UserMessage("안녕하세요! 간단히 자기소개 해주세요.")
        
        // 2. Prompt 생성
        val prompt = Prompt(userMessage)
        
        // 3. ChatModel 호출
        val response = chatModel.call(prompt)
        
        // 4. 응답 추출 (null-safe)
        return response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
    }
    
    /**
     * POST 요청으로 메시지를 받아 응답하는 예제
     * POST http://localhost:8080/api/simple/chat
     * Body: {"message": "Spring AI에 대해 설명해주세요"}
     */
    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val prompt = Prompt(UserMessage(request.message))
        val response = chatModel.call(prompt)
        
        return ChatResponse(
            message = response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
        )
    }
    
    /**
     * 안전한 응답 처리 예제 (여러 방법)
     */
    @PostMapping("/safe")
    fun safeChat(@RequestBody request: ChatRequest): String {
        val prompt = Prompt(UserMessage(request.message))
        
        // 방법 1: Safe call operator 사용
        return try {
            val response = chatModel.call(prompt)
            response.results.firstOrNull()?.output?.text ?: "응답 없음"
        } catch (e: Exception) {
            "오류 발생: ${e.message}"
        }
    }
}

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val message: String
)

