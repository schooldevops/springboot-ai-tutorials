package com.example.productimagetag.service

import com.example.productimagetag.dto.ProductTags
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service

@Service
class ImageAnalysisService(
    private val chatModel: ChatModel
) {
    
    private val objectMapper = jacksonObjectMapper()
    
    fun analyzeProductImage(imageBytes: ByteArray): ProductTags {
        if (imageBytes.isEmpty()) {
            throw IllegalArgumentException("이미지 데이터가 비어있습니다")
        }
        
        val systemPrompt = """
            당신은 상품 이미지 분석 전문가입니다.
            이미지를 분석하여 다음 정보를 JSON 형식으로 추출해주세요:
            - colors: 주요 색상 목록 (한글)
            - style: 스타일 (예: 모던, 클래식, 캐주얼 등)
            - features: 특징 목록 (예: 심플, 고급스러움 등)
            - category: 카테고리 (예: 의류, 가전, 가구 등)
            - tags: 마케팅 태그 목록 (# 포함)
            - description: 간단한 설명
            
            반드시 다음 JSON 형식으로 응답하세요:
            {
              "colors": ["색상1", "색상2"],
              "style": "스타일",
              "features": ["특징1", "특징2"],
              "category": "카테고리",
              "tags": ["#태그1", "#태그2"],
              "description": "설명"
            }
        """.trimIndent()
        
        val userMessage = UserMessage("이 상품 이미지를 분석해주세요.")
        
        val prompt = Prompt(listOf(SystemMessage(systemPrompt), userMessage))
        val response = chatModel.call(prompt)
        
        val content = response.result.output.content
        
        return try {
            objectMapper.readValue<ProductTags>(content)
        } catch (e: Exception) {
            // Fallback: extract JSON from response
            val jsonStart = content.indexOf("{")
            val jsonEnd = content.lastIndexOf("}") + 1
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonContent = content.substring(jsonStart, jsonEnd)
                objectMapper.readValue<ProductTags>(jsonContent)
            } else {
                throw IllegalStateException("JSON 파싱 실패: ${e.message}")
            }
        }
    }
}

