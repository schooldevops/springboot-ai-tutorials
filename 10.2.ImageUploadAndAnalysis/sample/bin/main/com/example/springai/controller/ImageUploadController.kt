package com.example.springai.controller

import com.example.springai.service.ImageAnalysisService
import com.example.springai.service.ImageValidationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * 이미지 업로드 및 분석을 위한 REST Controller
 */
@RestController
@RequestMapping("/api/image")
class ImageUploadController(
    private val imageAnalysisService: ImageAnalysisService,
    private val imageValidationService: ImageValidationService
) {
    
    /**
     * 이미지를 업로드하고 분석합니다.
     */
    @PostMapping("/upload-and-analyze")
    fun uploadAndAnalyze(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("question", defaultValue = "이 이미지를 자세히 설명해주세요.") question: String
    ): ResponseEntity<Map<String, Any>> {
        // 파일 검증
        val validation = imageValidationService.validateImage(file)
        if (!validation.isValid) {
            return ResponseEntity.badRequest().body(mapOf<String, Any>(
                "success" to false,
                "error" to validation.message
            ))
        }
        
        try {
            // 이미지 분석
            val analysis = imageAnalysisService.analyzeImage(file, question)
            
            return ResponseEntity.ok(mapOf<String, Any>(
                "success" to true,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size,
                "contentType" to (file.contentType ?: "unknown"),
                "question" to question,
                "analysis" to analysis
            ))
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf<String, Any>(
                "success" to false,
                "error" to "이미지 분석 중 오류 발생: ${e.message ?: "Unknown error"}"
            ))
        }
    }
    
    /**
     * 이미지를 업로드하고 자동으로 설명을 생성합니다.
     */
    @PostMapping("/upload-and-describe")
    fun uploadAndDescribe(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        // 파일 검증
        val validation = imageValidationService.validateImage(file)
        if (!validation.isValid) {
            return ResponseEntity.badRequest().body(mapOf<String, Any>(
                "success" to false,
                "error" to validation.message
            ))
        }
        
        try {
            // 이미지 설명 생성
            val description = imageAnalysisService.describeImage(file)
            
            return ResponseEntity.ok(mapOf<String, Any>(
                "success" to true,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size,
                "contentType" to (file.contentType ?: "unknown"),
                "description" to description
            ))
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf<String, Any>(
                "success" to false,
                "error" to "이미지 분석 중 오류 발생: ${e.message ?: "Unknown error"}"
            ))
        }
    }
    
    /**
     * 이미지를 업로드하고 질문에 답변합니다.
     */
    @PostMapping("/upload-and-ask")
    fun uploadAndAsk(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("question") question: String
    ): ResponseEntity<Map<String, Any>> {
        // 파일 검증
        val validation = imageValidationService.validateImage(file)
        if (!validation.isValid) {
            return ResponseEntity.badRequest().body(mapOf<String, Any>(
                "success" to false,
                "error" to validation.message
            ))
        }
        
        try {
            // 이미지에 대한 질문에 답변
            val answer = imageAnalysisService.askQuestionAboutImage(file, question)
            
            return ResponseEntity.ok(mapOf<String, Any>(
                "success" to true,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size,
                "question" to question,
                "answer" to answer
            ))
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf<String, Any>(
                "success" to false,
                "error" to "이미지 분석 중 오류 발생: ${e.message ?: "Unknown error"}"
            ))
        }
    }
    
    /**
     * 이미지를 업로드하고 상세 분석합니다.
     */
    @PostMapping("/upload-and-analyze-details")
    fun uploadAndAnalyzeDetails(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        // 파일 검증
        val validation = imageValidationService.validateImage(file)
        if (!validation.isValid) {
            return ResponseEntity.badRequest().body(mapOf<String, Any>(
                "success" to false,
                "error" to validation.message
            ))
        }
        
        try {
            // 이미지 상세 분석
            val analysis = imageAnalysisService.analyzeImageDetails(file)
            
            return ResponseEntity.ok(mapOf<String, Any>(
                "success" to true,
                "filename" to (file.originalFilename ?: "unknown"),
                "size" to file.size,
                "contentType" to (file.contentType ?: "unknown"),
                "analysis" to analysis
            ))
        } catch (e: Exception) {
            return ResponseEntity.status(500).body(mapOf<String, Any>(
                "success" to false,
                "error" to "이미지 분석 중 오류 발생: ${e.message ?: "Unknown error"}"
            ))
        }
    }
    
    /**
     * 지원하는 파일 형식 및 제한사항 조회
     */
    @GetMapping("/info")
    fun getImageInfo(): Map<String, Any> {
        return mapOf<String, Any>(
            "supportedFormats" to imageValidationService.getSupportedFormats(),
            "maxFileSizeMB" to imageValidationService.getMaxFileSizeMB(),
            "note" to "PNG, JPEG, GIF, WebP 형식의 이미지를 업로드할 수 있습니다."
        )
    }
    
    /**
     * 간단한 테스트 엔드포인트
     */
    @GetMapping("/test")
    fun test(): Map<String, String> {
        return mapOf(
            "status" to "OK",
            "message" to "Image Upload and Analysis API is running",
            "endpoints" to """
                POST /api/image/upload-and-analyze - 이미지 업로드 및 분석 (질문 포함 가능)
                POST /api/image/upload-and-describe - 이미지 업로드 및 설명 생성
                POST /api/image/upload-and-ask - 이미지 업로드 및 질문 답변
                POST /api/image/upload-and-analyze-details - 이미지 업로드 및 상세 분석
                GET /api/image/info - 지원하는 파일 형식 및 제한사항 조회
            """.trimIndent(),
            "note" to "Vision API를 사용하려면 GPT-4o 또는 Claude 3 등 Vision 지원 모델이 필요합니다."
        )
    }
}

