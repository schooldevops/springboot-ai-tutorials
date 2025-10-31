package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.SimilarityRequest
import com.example.springai.util.SimilarityUtils

/**
 * 기본 텍스트 유사도 계산 컨트롤러
 */
@RestController
@RequestMapping("/api/similarity")
class BasicSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 두 텍스트 간 유사도 계산
     * POST http://localhost:8080/api/similarity/calculate
     * Body: {"text1": "Spring AI는 프레임워크입니다.", "text2": "Spring AI는 라이브러리입니다."}
     */
    @PostMapping("/calculate")
    fun calculateSimilarity(@RequestBody request: SimilarityRequest): Map<String, Any> {
        val embedding1 = embeddingModel.embed(request.text1)
        val embedding2 = embeddingModel.embed(request.text2)
        
        val similarity = SimilarityUtils.cosineSimilarity(embedding1, embedding2)
        val interpretation = SimilarityUtils.interpretSimilarity(similarity)
        
        return mapOf(
            "text1" to request.text1,
            "text2" to request.text2,
            "similarity" to similarity,
            "similarityPercent" to (similarity * 100),
            "interpretation" to interpretation
        )
    }
}

