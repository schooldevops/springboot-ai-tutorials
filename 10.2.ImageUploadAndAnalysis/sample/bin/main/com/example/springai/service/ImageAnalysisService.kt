package com.example.springai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.Base64

/**
 * 이미지 분석 서비스
 * 
 * MultipartFile로 업로드된 이미지를 Base64로 인코딩하여
 * Spring AI를 통해 분석합니다.
 */
@Service
class ImageAnalysisService(
    private val chatModel: ChatModel
) {
    
    /**
     * 이미지를 분석하고 설명을 생성합니다.
     */
    fun analyzeImage(
        file: MultipartFile,
        question: String = "이 이미지를 자세히 설명해주세요."
    ): String {
        try {
            // MultipartFile을 Base64로 변환
            val imageBytes = file.bytes
            val base64Image = Base64.getEncoder().encodeToString(imageBytes)
            
            // MimeType 확인
            val mimeType = file.contentType ?: "image/jpeg"
            
            // Spring AI에 전송
            val promptText = """
                $question
                
                이미지 데이터 (Base64): $base64Image
                이미지 형식: $mimeType
                
                주의: 실제 Vision API를 사용하려면 GPT-4o 또는 Claude 3 등 Vision 지원 모델이 필요하며,
                모델별로 이미지 전송 방식이 다를 수 있습니다.
            """.trimIndent()
            
            val userMessage = UserMessage(promptText)
            val prompt = Prompt(userMessage)
            val response = chatModel.call(prompt)
            
            return response.results.firstOrNull()?.output?.text 
                ?: "이미지 분석 결과를 생성할 수 없습니다."
        } catch (e: Exception) {
            throw RuntimeException("이미지 분석 중 오류 발생: ${e.message}", e)
        }
    }
    
    /**
     * 이미지를 설명합니다.
     */
    fun describeImage(file: MultipartFile): String {
        return analyzeImage(
            file,
            "이 이미지를 자세히 설명해주세요. 이미지에 있는 주요 객체, 색상, 분위기 등을 포함해주세요."
        )
    }
    
    /**
     * 이미지에 대한 질문에 답변합니다.
     */
    fun askQuestionAboutImage(file: MultipartFile, question: String): String {
        return analyzeImage(file, question)
    }
    
    /**
     * 이미지를 상세 분석합니다.
     */
    fun analyzeImageDetails(file: MultipartFile): String {
        val analysisPrompt = """
            이 이미지를 분석해주세요:
            1. 이미지에 있는 주요 객체들
            2. 주요 색상과 색상 조합
            3. 이미지 스타일 (사진, 그림, 일러스트 등)
            4. 이미지의 분위기나 감정
            5. 이미지의 구도나 레이아웃
        """.trimIndent()
        
        return analyzeImage(file, analysisPrompt)
    }
}

