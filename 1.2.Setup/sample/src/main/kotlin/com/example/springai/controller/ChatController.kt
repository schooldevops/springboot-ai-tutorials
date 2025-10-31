package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatModel: ChatModel
) {
    
    /**
     * 간단한 테스트 엔드포인트
     * GET http://localhost:8080/api/chat/test
     */
    @GetMapping("/test")
    fun test(): String {
        val prompt = Prompt(UserMessage("안녕하세요! 간단히 자기소개 해주세요."))
        val response = chatModel.call(prompt)
        return response.result?.output?.text ?: "응답을 생성할 수 없습니다."
    }
    
    /**
     * 메시지를 받아서 AI에게 질문하는 엔드포인트
     * POST http://localhost:8080/api/chat
     * Body: {"message": "Spring AI에 대해 설명해주세요"}
     */
    @PostMapping
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val prompt = Prompt(UserMessage(request.message))
        val response = chatModel.call(prompt)
        
        return ChatResponse(
            message = response.result?.output?.text ?: "응답을 생성할 수 없습니다."
        )
    }
}

data class ChatRequest(
    val message: String
)

data class ChatResponse(
    val message: String
)

