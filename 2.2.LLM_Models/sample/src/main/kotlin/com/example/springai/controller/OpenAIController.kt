package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*

/**
 * OpenAI 모델 사용 예제
 * OpenAI Starter를 사용할 때의 기본 컨트롤러
 */
@RestController
@RequestMapping("/api/openai")
class OpenAIController(
    private val chatModel: ChatModel  // OpenAI ChatModel 자동 주입
) {
    
    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val prompt = Prompt(UserMessage(request.message))
        val response = chatModel.call(prompt)
        
        return ChatResponse(
            model = "OpenAI",
            message = response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
        )
    }
    
    @GetMapping("/test")
    fun test(): String {
        val prompt = Prompt(UserMessage("안녕하세요! 간단히 자기소개 해주세요."))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val model: String,
    val message: String
)

