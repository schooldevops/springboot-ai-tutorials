package com.example.springai.controller

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.*
import org.springframework.web.bind.annotation.*
import com.example.springai.model.MessageRequest

/**
 * 기본적인 메시지 타입 사용법을 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/basic")
class BasicMessageController(
    private val chatModel: ChatModel
) {
    
    /**
     * SystemMessage만 사용하는 예제
     * GET http://localhost:8080/api/basic/system-only
     */
    @GetMapping("/system-only")
    fun systemOnlyChat(): String {
        val systemMessage = SystemMessage("당신은 친절한 어시스턴트입니다.")
        val prompt = Prompt(systemMessage)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * SystemMessage + UserMessage 조합 예제
     * POST http://localhost:8080/api/basic/system-user
     * Body: {"message": "안녕하세요!"}
     */
    @PostMapping("/system-user")
    fun systemUserChat(@RequestBody request: MessageRequest): String {
        val messages = listOf(
            SystemMessage("당신은 친절한 어시스턴트입니다."),
            UserMessage(request.message)
        )
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
    
    /**
     * 대화 이력을 포함한 예제
     * POST http://localhost:8080/api/basic/conversation
     */
    @PostMapping("/conversation")
    fun conversation(): String {
        val messages = listOf(
            SystemMessage("당신은 친절한 어시스턴트입니다."),
            UserMessage("안녕하세요!"),
            AssistantMessage("안녕하세요! 무엇을 도와드릴까요?"),
            UserMessage("Spring AI에 대해 설명해주세요")
        )
        val prompt = Prompt(messages)
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답 없음"
    }
}

