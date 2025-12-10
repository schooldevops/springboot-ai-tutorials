package com.example.function.controller

import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.*

/** Sample 01: Basic Function Calling */
@RestController
@RequestMapping("/api/chat")
class FunctionController(private val chatClient: ChatClient) {

    @PostMapping("/ask")
    fun ask(@RequestBody request: Map<String, String>): Map<String, String> {
        val question = request["question"] ?: throw IllegalArgumentException("Question required")

        val response = chatClient.prompt().user(question).call().content()

        return mapOf("response" to response)
    }
}
