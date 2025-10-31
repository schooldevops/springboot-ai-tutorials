package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.EmbedRequest

/**
 * 안전한 임베딩 생성 컨트롤러 (에러 처리 포함)
 */
@RestController
@RequestMapping("/api/safe-embedding")
class SafeEmbeddingController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 안전한 임베딩 생성 (에러 처리 포함)
     * POST http://localhost:8080/api/safe-embedding/embed
     */
    @PostMapping("/embed")
    fun safeEmbed(@RequestBody request: EmbedRequest): Map<String, Any> {
        return try {
            val embedding = embeddingModel.embed(request.text)
            
            mapOf(
                "success" to true,
                "text" to request.text,
                "dimension" to embedding.size,
                "sample" to embedding.take(5).map { it.toDouble() }
            )
        } catch (e: Exception) {
            mapOf(
                "success" to false,
                "error" to (e.message ?: "알 수 없는 오류"),
                "errorType" to e.javaClass.simpleName
            )
        }
    }
}

