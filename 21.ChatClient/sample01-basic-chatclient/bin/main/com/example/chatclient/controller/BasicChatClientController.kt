package com.example.chatclient.controller

import com.example.chatclient.model.ChatRequest
import com.example.chatclient.model.ChatResponse
import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.*

/**
 * Basic ChatClient 사용 예제 컨트롤러
 *
 * 이 컨트롤러는 ChatClient의 기본 사용법을 보여줍니다:
 * 1. 간단한 문자열 프롬프트
 * 2. Fluent API 사용
 * 3. ChatResponse 메타데이터 접근
 */
@RestController
@RequestMapping("/api/basic")
class BasicChatClientController(private val chatClient: ChatClient) {

    /** 1. 가장 간단한 사용법: prompt(String) POST /api/basic/simple Body: {"message": "Your question"} */
    @PostMapping("/simple")
    fun simplePrompt(@RequestBody request: ChatRequest): ChatResponse {
        val content = chatClient.prompt(request.message).call().content()

        return ChatResponse(content = content ?: "")
    }

    /**
     * 2. Fluent API 사용: prompt().user() POST /api/basic/fluent Body: {"message": "Your question"}
     */
    @PostMapping("/fluent")
    fun fluentApi(@RequestBody request: ChatRequest): ChatResponse {
        val content = chatClient.prompt().user(request.message).call().content()

        return ChatResponse(content = content ?: "")
    }

    /** 3. ChatResponse로 메타데이터 접근 POST /api/basic/metadata Body: {"message": "Your question"} */
    @PostMapping("/metadata")
    fun withMetadata(@RequestBody request: ChatRequest): ChatResponse {
        val chatResponse = chatClient.prompt(request.message).call().chatResponse()

        val content = chatResponse.results.firstOrNull()?.output?.content ?: ""
        val model = chatResponse.metadata?.model

        // 토큰 사용량 추출 (사용 가능한 경우)
        val tokensUsed = chatResponse.metadata?.usage?.totalTokens?.toInt()

        return ChatResponse(content = content, model = model, tokensUsed = tokensUsed)
    }

    /**
     * 4. System 메시지와 User 메시지 함께 사용 POST /api/basic/with-system Body: {"message": "Your question"}
     */
    @PostMapping("/with-system")
    fun withSystemMessage(@RequestBody request: ChatRequest): ChatResponse {
        val content =
                chatClient
                        .prompt()
                        .system("You are a helpful assistant. Answer concisely and clearly.")
                        .user(request.message)
                        .call()
                        .content()

        return ChatResponse(content = content ?: "")
    }
}
