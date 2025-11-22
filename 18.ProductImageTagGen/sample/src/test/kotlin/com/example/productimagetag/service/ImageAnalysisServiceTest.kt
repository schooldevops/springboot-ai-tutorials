package com.example.productimagetag.service

import com.example.productimagetag.dto.ProductTags
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.prompt.Prompt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ImageAnalysisServiceTest {
    
    private val chatModel = mockk<ChatModel>()
    private val imageAnalysisService = ImageAnalysisService(chatModel)
    
    @Test
    fun `should analyze image and return product tags`() {
        // given
        val imageBytes = "test-image".toByteArray()
        val mockJsonResponse = """
            {
              "colors": ["빨강", "검정"],
              "style": "모던",
              "features": ["심플", "고급스러움"],
              "category": "의류",
              "tags": ["#레드", "#모던"],
              "description": "빨간색 모던 스타일"
            }
        """.trimIndent()
        
        val mockGeneration = Generation(AssistantMessage(mockJsonResponse))
        val mockResponse = ChatResponse(listOf(mockGeneration))
        
        every { chatModel.call(any<Prompt>()) } returns mockResponse
        
        // when
        val result = imageAnalysisService.analyzeProductImage(imageBytes)
        
        // then
        assertNotNull(result)
        assertEquals(2, result.colors.size)
        assertTrue(result.colors.contains("빨강"))
        assertEquals("모던", result.style)
        assertEquals("의류", result.category)
    }
    
    @Test
    fun `should handle empty image`() {
        // given
        val emptyBytes = ByteArray(0)
        
        // when & then
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            imageAnalysisService.analyzeProductImage(emptyBytes)
        }
    }
}
