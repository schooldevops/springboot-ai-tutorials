package com.example.allinonechatbot.dto

data class ChatRequest(
    val sessionId: String,
    val message: String
)

data class ChatResponse(
    val message: String,
    val sessionId: String,
    val source: String = "unknown" // "rag", "function", "memory", "general"
)
