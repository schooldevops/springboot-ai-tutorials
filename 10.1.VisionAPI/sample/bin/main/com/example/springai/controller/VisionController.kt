package com.example.springai.controller

import com.example.springai.service.VisionService
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * Vision API를 테스트하기 위한 REST Controller
 */
@RestController
@RequestMapping("/api/vision")
class VisionController(
    private val visionService: VisionService
) {
    
    /**
     * 이미지를 분석하고 설명을 생성합니다.
     */
    @PostMapping("/analyze")
    fun analyzeImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("question", defaultValue = "이 이미지를 자세히 설명해주세요.") question: String
    ): Map<String, Any> {
        try {
            val imageResource = file.resource
            val description = visionService.analyzeImage(imageResource, question)
            
            return mapOf<String, Any>(
                "success" to true,
                "question" to question,
                "description" to description,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size,
                "contentType" to (file.contentType ?: "unknown")
            )
        } catch (e: Exception) {
            return mapOf<String, Any>(
                "success" to false,
                "error" to (e.message ?: "Unknown error"),
                "filename" to (file.originalFilename ?: "unknown")
            )
        }
    }
    
    /**
     * 이미지를 설명합니다.
     */
    @PostMapping("/describe")
    fun describeImage(
        @RequestParam("file") file: MultipartFile
    ): Map<String, Any> {
        try {
            val imageResource = file.resource
            val description = visionService.describeImage(imageResource)
            
            return mapOf<String, Any>(
                "success" to true,
                "description" to description,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size
            )
        } catch (e: Exception) {
            return mapOf<String, Any>(
                "success" to false,
                "error" to (e.message ?: "Unknown error"),
                "filename" to (file.originalFilename ?: "unknown")
            )
        }
    }
    
    /**
     * 이미지에 대한 질문에 답변합니다.
     */
    @PostMapping("/ask")
    fun askQuestionAboutImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("question") question: String
    ): Map<String, Any> {
        try {
            val imageResource = file.resource
            val answer = visionService.askQuestionAboutImage(imageResource, question)
            
            return mapOf<String, Any>(
                "success" to true,
                "question" to question,
                "answer" to answer,
                "filename" to (file.originalFilename ?: "unknown")
            )
        } catch (e: Exception) {
            return mapOf<String, Any>(
                "success" to false,
                "error" to (e.message ?: "Unknown error"),
                "question" to question,
                "filename" to (file.originalFilename ?: "unknown")
            )
        }
    }
    
    /**
     * 이미지를 상세 분석합니다.
     */
    @PostMapping("/analyze-details")
    fun analyzeImageDetails(
        @RequestParam("file") file: MultipartFile
    ): Map<String, Any> {
        try {
            val imageResource = file.resource
            val analysis = visionService.analyzeImageDetails(imageResource)
            
            return mapOf<String, Any>(
                "success" to true,
                "analysis" to analysis,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size
            )
        } catch (e: Exception) {
            return mapOf<String, Any>(
                "success" to false,
                "error" to (e.message ?: "Unknown error"),
                "filename" to (file.originalFilename ?: "unknown")
            )
        }
    }
    
    /**
     * 간단한 테스트 엔드포인트
     */
    @GetMapping("/test")
    fun test(): Map<String, String> {
            return mapOf<String, String>(
                "status" to "OK",
                "message" to "Vision API is running",
                "endpoints" to """
                POST /api/vision/analyze - 이미지 분석 (질문 포함 가능)
                POST /api/vision/describe - 이미지 설명 생성
                POST /api/vision/ask - 이미지에 대한 질문
                POST /api/vision/analyze-details - 이미지 상세 분석
            """.trimIndent(),
                "note" to "Vision API를 사용하려면 GPT-4o 또는 Claude 3 등 Vision 지원 모델이 필요합니다."
            )
    }
}

