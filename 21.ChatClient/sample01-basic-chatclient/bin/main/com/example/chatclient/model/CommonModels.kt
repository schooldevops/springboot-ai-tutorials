package com.example.chatclient.model

/** 공통 요청/응답 모델 */
data class ChatRequest(val message: String)

data class ChatResponse(
        val content: String,
        val model: String? = null,
        val tokensUsed: Int? = null
)
