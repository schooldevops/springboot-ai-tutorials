package com.example.simplechatbot.dto

data class ChatResponse(
    val response: String,
    val timestamp: Long = System.currentTimeMillis()
)
