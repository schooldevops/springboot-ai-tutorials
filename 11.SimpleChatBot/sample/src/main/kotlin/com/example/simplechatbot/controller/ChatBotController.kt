package com.example.simplechatbot.controller

import com.example.simplechatbot.dto.ChatRequest
import com.example.simplechatbot.dto.ChatResponse
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatBotController(
    private val chatModel: ChatModel
) {

    /**
     * 1. 간단한 채팅 API
     * ChatModel을 사용하여 기본적인 질의응답을 처리합니다.
     */
    @PostMapping("/simple")
    fun simpleChat(@RequestBody request: ChatRequest): ChatResponse {
        // ChatModel의 call 메서드로 간단하게 호출
        val response = chatModel.call(request.message)
        return ChatResponse(response)
    }

    /**
     * 2. 템플릿 기반 채팅 API
     * PromptTemplate을 사용하여 동적으로 프롬프트를 생성합니다.
     */
    @PostMapping("/template")
    fun templateChat(@RequestBody request: ChatRequest): ChatResponse {
        // PromptTemplate 생성
        val template = PromptTemplate(
            "당신은 {topic}에 대한 전문가입니다. 다음 질문에 답변해주세요: {question}"
        )
        
        // 변수 주입하여 Prompt 생성
        val prompt = template.create(mapOf(
            "topic" to (request.topic ?: "일반"),
            "question" to request.message
        ))
        
        // ChatModel 호출 및 응답 추출
        val chatResponse = chatModel.call(prompt)
        val answer = chatResponse.result.output.content
        
        return ChatResponse(answer)
    }

    /**
     * 3. 역할 기반 채팅 API
     * SystemMessage와 UserMessage를 사용하여 AI의 역할을 정의하고 질문을 전달합니다.
     */
    @PostMapping("/role")
    fun roleBasedChat(@RequestBody request: ChatRequest): ChatResponse {
        // SystemMessage로 AI의 역할 정의
        val systemMessage = SystemMessage(
            request.role ?: "당신은 친절하고 도움이 되는 AI 어시스턴트입니다."
        )
        
        // UserMessage로 사용자 질문 전달
        val userMessage = UserMessage(request.message)
        
        // Prompt 생성 (메시지 리스트)
        val prompt = Prompt(listOf(systemMessage, userMessage))
        
        // ChatModel 호출 및 응답 추출
        val chatResponse = chatModel.call(prompt)
        val answer = chatResponse.result.output.content
        
        return ChatResponse(answer)
    }

    /**
     * 헬스 체크 엔드포인트
     */
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Simple ChatBot API",
            "timestamp" to System.currentTimeMillis().toString()
        )
    }
}
