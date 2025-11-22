package com.example.simplechatbot.dto

data class ChatRequest(
    val message: String,
    val topic: String? = null,
    val role: String? = null
)
