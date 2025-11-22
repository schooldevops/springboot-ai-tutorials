package com.example.weatheralarm.controller

import com.example.weatheralarm.dto.ChatRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse as AIChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.prompt.Prompt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class WeatherControllerTest {
    
    private val chatModel = mockk<ChatModel>()
    private val controller = WeatherController(chatModel)
    
    @Test
    fun `should return chat response with weather information`() {
        // given
        val request = ChatRequest(message = "오늘 서울 날씨 어때?")
        val mockGeneration = Generation(AssistantMessage("서울의 현재 날씨는 맑고 기온은 15도입니다."))
        val mockResponse = AIChatResponse(listOf(mockGeneration))
        
        every { chatModel.call(any<Prompt>()) } returns mockResponse
        
        // when
        val result = controller.chat(request)
        
        // then
        assertNotNull(result)
        assertEquals("서울의 현재 날씨는 맑고 기온은 15도입니다.", result.message)
    }
    
    @Test
    fun `should handle empty message`() {
        // given
        val request = ChatRequest(message = "")
        val mockGeneration = Generation(AssistantMessage("질문을 입력해주세요."))
        val mockResponse = AIChatResponse(listOf(mockGeneration))
        
        every { chatModel.call(any<Prompt>()) } returns mockResponse
        
        // when
        val result = controller.chat(request)
        
        // then
        assertNotNull(result)
    }
}
