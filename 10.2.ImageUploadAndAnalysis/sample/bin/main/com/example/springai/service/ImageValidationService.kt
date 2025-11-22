package com.example.springai.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

/**
 * 이미지 파일 검증 서비스
 */
@Service
class ImageValidationService {
    
    private val allowedContentTypes = setOf(
        "image/png",
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/webp"
    )
    
    private val maxFileSize = 20 * 1024 * 1024 // 20MB
    
    /**
     * 이미지 파일을 검증합니다.
     */
    fun validateImage(file: MultipartFile): ValidationResult {
        // 파일이 비어있는지 확인
        if (file.isEmpty) {
            return ValidationResult(false, "파일이 비어있습니다.")
        }
        
        // 파일 크기 확인
        if (file.size > maxFileSize) {
            return ValidationResult(
                false, 
                "파일 크기가 너무 큽니다. 최대 ${maxFileSize / 1024 / 1024}MB까지 지원합니다."
            )
        }
        
        // 파일 형식 확인
        val contentType = file.contentType
        if (contentType == null || !allowedContentTypes.contains(contentType.lowercase())) {
            return ValidationResult(
                false, 
                "지원하지 않는 파일 형식입니다. PNG, JPEG, GIF, WebP만 지원합니다. (현재: $contentType)"
            )
        }
        
        return ValidationResult(true, "검증 성공")
    }
    
    /**
     * 지원하는 이미지 형식 목록을 반환합니다.
     */
    fun getSupportedFormats(): List<String> {
        return allowedContentTypes.toList()
    }
    
    /**
     * 최대 파일 크기를 반환합니다 (MB).
     */
    fun getMaxFileSizeMB(): Int {
        return maxFileSize / 1024 / 1024
    }
}

/**
 * 검증 결과 데이터 클래스
 */
data class ValidationResult(
    val isValid: Boolean,
    val message: String
)

