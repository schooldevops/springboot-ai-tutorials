package com.example.wikichatbot.dto

/**
 * 채팅 요청
 */
data class ChatRequest(
    val question: String,
    val topK: Int? = 3
)

/**
 * 채팅 응답
 */
data class ChatResponse(
    val question: String,
    val answer: String,
    val sources: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 문서 인제스트 요청
 */
data class IngestRequest(
    val directory: String? = null,
    val filePath: String? = null
)
