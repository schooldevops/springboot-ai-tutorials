package com.example.conversationalchatbot.dto

data class ChatRequest(
    val sessionId: String,
    val message: String
)

data class ChatResponse(
    val message: String,
    val sessionId: String
)
