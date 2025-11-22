package com.example.ragchatbot.dto

data class ChatRequest(
    val question: String,
    val topK: Int? = 3
)

data class ChatResponse(
    val question: String,
    val answer: String,
    val sources: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
