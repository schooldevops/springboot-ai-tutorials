package com.example.springai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service

/**
 * Function Calling을 활용한 서비스
 * 
 * 주의: Spring AI 1.0.0-M6에서 Ollama는 Function Calling을 완전히 지원하지 않을 수 있습니다.
 * OpenAI GPT-4나 GPT-3.5-turbo를 사용하는 것을 권장합니다.
 * 
 * 함수는 FunctionConfiguration에서 Bean으로 등록되어 있으며,
 * Spring AI가 자동으로 감지하여 사용할 수 있습니다.
 */
@Service
class FunctionCallService(
    private val chatModel: ChatModel
) {
    
    /**
     * 계산기 함수를 사용하여 호출
     * 
     * 함수는 Bean으로 등록되어 있으므로, 일반 Prompt로 호출하면
     * LLM이 필요에 따라 함수를 호출할 수 있습니다.
     * 
     * 주의: Ollama는 Function Calling을 지원하지 않을 수 있으므로,
     * 이 경우 함수가 호출되지 않고 일반 응답만 반환됩니다.
     */
    fun callWithCalculator(userMessage: String): String {
        // 함수는 Bean으로 등록되어 있으므로 일반 Prompt로 호출
        // LLM이 필요에 따라 calculatorFunction을 호출할 수 있습니다
        val prompt = Prompt(UserMessage(userMessage))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
    }
    
    /**
     * 시간 조회 함수를 사용하여 호출
     */
    fun callWithTime(userMessage: String): String {
        val prompt = Prompt(UserMessage(userMessage))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
    }
    
    /**
     * 여러 함수를 함께 사용하여 호출
     * LLM이 상황에 맞게 적절한 함수를 선택하여 호출합니다.
     */
    fun callWithMultipleFunctions(userMessage: String): String {
        // 모든 함수가 Bean으로 등록되어 있으므로, LLM이 필요에 따라 선택하여 호출합니다
        val prompt = Prompt(UserMessage(userMessage))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
    }
    
    /**
     * 함수 없이 일반 호출 (비교용)
     */
    fun callWithoutFunction(userMessage: String): String {
        val prompt = Prompt(UserMessage(userMessage))
        val response = chatModel.call(prompt)
        return response.results.firstOrNull()?.output?.text ?: "응답을 생성할 수 없습니다."
    }
}

