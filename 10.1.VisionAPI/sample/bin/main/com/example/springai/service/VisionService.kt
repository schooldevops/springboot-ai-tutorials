package com.example.springai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import java.util.Base64

/**
 * Vision API를 활용한 이미지 분석 서비스
 * 
 * 주의: Vision API를 사용하려면 GPT-4o, GPT-4 Turbo, Claude 3 등
 * Vision을 지원하는 모델을 사용해야 합니다.
 * 
 * Spring AI 1.0.0-M6에서는 Vision API가 아직 완전히 지원되지 않을 수 있습니다.
 * 이 샘플은 Vision API 사용 패턴을 보여주며, 실제 구현은 모델별로 다를 수 있습니다.
 */
@Service
class VisionService(
    private val chatModel: ChatModel
) {
    
    /**
     * 이미지를 분석하고 설명을 생성합니다.
     * 
     * 주의: Spring AI 1.0.0-M6에서는 Media 클래스가 없을 수 있으므로,
     * 이미지를 Base64로 인코딩하여 텍스트로 전송하는 방식을 사용합니다.
     * 실제 Vision API 사용은 모델별로 다를 수 있습니다.
     */
    fun analyzeImage(imageResource: Resource, question: String = "이 이미지를 자세히 설명해주세요."): String {
        try {
            // 이미지를 Base64로 인코딩
            val imageBytes = StreamUtils.copyToByteArray(imageResource.inputStream)
            val base64Image = Base64.getEncoder().encodeToString(imageBytes)
            
            // MimeType 자동 감지
            val mimeType = getMimeTypeFromResource(imageResource)
            
            // Vision API를 사용하려면 모델별로 다른 방식이 필요할 수 있습니다.
            // 현재는 Base64 인코딩된 이미지를 텍스트로 포함하는 방식으로 시뮬레이션합니다.
            // 실제 Vision API 사용 시에는 모델별 문서를 참고하세요.
            val promptText = """
                $question
                
                이미지 데이터 (Base64): $base64Image
                이미지 형식: $mimeType
                
                주의: 실제 Vision API를 사용하려면 GPT-4o 또는 Claude 3 등 Vision 지원 모델이 필요하며,
                모델별로 이미지 전송 방식이 다를 수 있습니다.
            """.trimIndent()
            
            // UserMessage 생성
            val userMessage = UserMessage(promptText)
            
            // ChatModel 호출
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
    fun describeImage(imageResource: Resource): String {
        return analyzeImage(
            imageResource,
            "이 이미지를 자세히 설명해주세요. 이미지에 있는 주요 객체, 색상, 분위기 등을 포함해주세요."
        )
    }
    
    /**
     * 이미지에 대한 질문에 답변합니다.
     */
    fun askQuestionAboutImage(imageResource: Resource, question: String): String {
        return analyzeImage(imageResource, question)
    }
    
    /**
     * 이미지를 분석합니다 (객체, 색상, 스타일 등).
     */
    fun analyzeImageDetails(imageResource: Resource): String {
        val analysisPrompt = """
            이 이미지를 분석해주세요:
            1. 이미지에 있는 주요 객체들
            2. 주요 색상과 색상 조합
            3. 이미지 스타일 (사진, 그림, 일러스트 등)
            4. 이미지의 분위기나 감정
            5. 이미지의 구도나 레이아웃
        """.trimIndent()
        
        return analyzeImage(imageResource, analysisPrompt)
    }
    
    /**
     * 여러 이미지를 비교합니다.
     */
    fun compareImages(
        imageResource1: Resource,
        imageResource2: Resource,
        question: String = "이 두 이미지를 비교해주세요."
    ): String {
        try {
            // 첫 번째 이미지
            val imageBytes1 = StreamUtils.copyToByteArray(imageResource1.inputStream)
            val base64Image1 = Base64.getEncoder().encodeToString(imageBytes1)
            val mimeType1 = getMimeTypeFromResource(imageResource1)
            
            // 두 번째 이미지
            val imageBytes2 = StreamUtils.copyToByteArray(imageResource2.inputStream)
            val base64Image2 = Base64.getEncoder().encodeToString(imageBytes2)
            val mimeType2 = getMimeTypeFromResource(imageResource2)
            
            // Vision API를 사용하려면 모델별로 다른 방식이 필요할 수 있습니다.
            val promptText = """
                $question
                
                첫 번째 이미지 데이터 (Base64): $base64Image1
                첫 번째 이미지 형식: $mimeType1
                
                두 번째 이미지 데이터 (Base64): $base64Image2
                두 번째 이미지 형식: $mimeType2
                
                주의: 실제 Vision API를 사용하려면 GPT-4o 또는 Claude 3 등 Vision 지원 모델이 필요하며,
                모델별로 이미지 전송 방식이 다를 수 있습니다.
            """.trimIndent()
            
            val userMessage = UserMessage(promptText)
            val prompt = Prompt(userMessage)
            val response = chatModel.call(prompt)
            
            return response.results.firstOrNull()?.output?.text 
                ?: "이미지 비교 결과를 생성할 수 없습니다."
        } catch (e: Exception) {
            throw RuntimeException("이미지 비교 중 오류 발생: ${e.message}", e)
        }
    }
    
    /**
     * Resource에서 MimeType을 자동으로 감지합니다.
     */
    private fun getMimeTypeFromResource(resource: Resource): String {
        val filename = resource.filename ?: ""
        return when {
            filename.endsWith(".png", ignoreCase = true) -> "image/png"
            filename.endsWith(".jpg", ignoreCase = true) -> "image/jpeg"
            filename.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            filename.endsWith(".gif", ignoreCase = true) -> "image/gif"
            filename.endsWith(".webp", ignoreCase = true) -> "image/webp"
            else -> "image/jpeg" // 기본값
        }
    }
    
    /**
     * 이미지 크기를 검증합니다.
     */
    fun validateImageSize(imageBytes: ByteArray, maxSizeMB: Int = 20): Boolean {
        val sizeMB = imageBytes.size / (1024.0 * 1024.0)
        return sizeMB <= maxSizeMB
    }
}

