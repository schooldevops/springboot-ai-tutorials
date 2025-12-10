package com.example.chatclient.model

data class ChatRequest(val message: String, val systemMessage: String? = null)

data class ChatResponse(val content: String)
