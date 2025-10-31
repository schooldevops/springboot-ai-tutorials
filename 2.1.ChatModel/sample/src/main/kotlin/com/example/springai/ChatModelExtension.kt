package com.example.springai

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage

/**
 * ChatModel의 확장 함수 예제
 * Kotlin의 확장 함수를 활용하여 더 간편하게 사용할 수 있음
 */

/**
 * 간단한 문자열 메시지를 받아서 응답을 반환하는 확장 함수
 */
fun ChatModel.simpleCall(message: String): String {
    val prompt = Prompt(UserMessage(message))
    return this.call(prompt)
        .results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
}

/**
 * 안전한 호출 (예외 처리 포함)
 */
fun ChatModel.safeCall(message: String): Result<String> {
    return runCatching {
        val prompt = Prompt(UserMessage(message))
        this.call(prompt)
            .results.firstOrNull()?.output?.text ?: throw IllegalStateException("응답 없음")
    }
}

/**
 * 여러 메시지를 받아서 응답을 반환하는 확장 함수
 */
fun ChatModel.multiCall(vararg messages: String): String {
    val userMessages = messages.map { UserMessage(it) }
    val prompt = Prompt(userMessages)
    return this.call(prompt)
        .results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
}

