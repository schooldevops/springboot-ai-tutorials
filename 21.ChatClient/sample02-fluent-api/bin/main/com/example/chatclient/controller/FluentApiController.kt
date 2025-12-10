package com.example.chatclient.controller

import com.example.chatclient.model.ChatRequest
import com.example.chatclient.model.ChatResponse
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.web.bind.annotation.*

/**
 * Sample 02: Fluent API Deep Dive
 *
 * ChatClient의 3가지 prompt() 오버로드 메서드 시연:
 * 1. prompt() - 빈 시작점
 * 2. prompt(String) - 편의 메서드
 * 3. prompt(Prompt) - Prompt 객체 전달
 */
@RestController
@RequestMapping("/api/fluent")
class FluentApiController(private val chatClient: ChatClient) {

    /** 1. prompt() - 빈 시작점으로 Fluent API 구성 POST /api/fluent/empty-start */
    @PostMapping("/empty-start")
    fun emptyStart(@RequestBody request: ChatRequest): ChatResponse {
        val content = chatClient.prompt().user(request.message).call().content()

        return ChatResponse(content = content ?: "")
    }

    /** 2. prompt(String) - 문자열 편의 메서드 POST /api/fluent/string-prompt */
    @PostMapping("/string-prompt")
    fun stringPrompt(@RequestBody request: ChatRequest): ChatResponse {
        val content = chatClient.prompt(request.message).call().content()

        return ChatResponse(content = content ?: "")
    }

    /** 3. prompt(Prompt) - Prompt 객체 전달 POST /api/fluent/prompt-object */
    @PostMapping("/prompt-object")
    fun promptObject(@RequestBody request: ChatRequest): ChatResponse {
        val prompt =
                if (request.systemMessage != null) {
                    Prompt(
                            listOf(
                                    SystemMessage(request.systemMessage),
                                    UserMessage(request.message)
                            )
                    )
                } else {
                    Prompt(UserMessage(request.message))
                }

        val content = chatClient.prompt(prompt).call().content()

        return ChatResponse(content = content ?: "")
    }

    /** 4. 메서드 체이닝 - system()과 user() 함께 사용 POST /api/fluent/chaining */
    @PostMapping("/chaining")
    fun methodChaining(@RequestBody request: ChatRequest): ChatResponse {
        val systemMsg = request.systemMessage ?: "You are a helpful assistant"

        val content = chatClient.prompt().system(systemMsg).user(request.message).call().content()

        return ChatResponse(content = content ?: "")
    }
}
