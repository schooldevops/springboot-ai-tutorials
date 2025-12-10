package com.example.chatmodel.controller

import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.web.bind.annotation.*

/**
 * Sample 01: Basic ChatModel Usage
 *
 * ChatModel의 기본 사용법 시연
 */
@RestController
@RequestMapping("/api/basic")
class BasicChatModelController(private val chatModel: ChatModel) {

    /** 1. 간단한 String 호출 GET /api/basic/simple */
    @GetMapping("/simple")
    fun simpleCall(@RequestParam message: String): String {
        return chatModel.call(message)
    }

    /** 2. Prompt 객체 사용 GET /api/basic/prompt */
    @GetMapping("/prompt")
    fun promptCall(@RequestParam message: String): Map<String, Any> {
        val prompt = Prompt(message)
        val chatResponse = chatModel.call(prompt)

        return mapOf(
                "content" to chatResponse.result.output.content,
                "model" to (chatResponse.metadata?.model ?: "unknown")
        )
    }

    /** 3. UserMessage 사용 GET /api/basic/user-message */
    @GetMapping("/user-message")
    fun userMessageCall(@RequestParam message: String): Map<String, Any> {
        val userMessage = UserMessage(message)
        val prompt = Prompt(userMessage)
        val chatResponse = chatModel.call(prompt)

        return mapOf(
                "content" to chatResponse.result.output.content,
                "finishReason" to (chatResponse.result.metadata?.finishReason ?: "unknown")
        )
    }

    /** 4. ChatResponse 전체 정보 GET /api/basic/full-response */
    @GetMapping("/full-response")
    fun fullResponse(@RequestParam message: String): Map<String, Any> {
        val chatResponse = chatModel.call(Prompt(message))

        return mapOf(
                "content" to chatResponse.result.output.content,
                "model" to (chatResponse.metadata?.model ?: "unknown"),
                "usage" to
                        mapOf(
                                "promptTokens" to (chatResponse.metadata?.usage?.promptTokens ?: 0),
                                "generationTokens" to
                                        (chatResponse.metadata?.usage?.generationTokens ?: 0),
                                "totalTokens" to (chatResponse.metadata?.usage?.totalTokens ?: 0)
                        )
        )
    }
}
