package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.ThresholdSimilarityRequest
import com.example.springai.util.SimilarityUtils

/**
 * 임계값 기반 유사도 필터링 컨트롤러
 */
@RestController
@RequestMapping("/api/similarity/threshold")
class ThresholdSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 임계값 이상의 유사 텍스트 찾기
     * POST http://localhost:8080/api/similarity/threshold/filter
     * Body: {"query": "쿼리 텍스트", "texts": ["텍스트1", "텍스트2"], "threshold": 0.7}
     */
    @PostMapping("/filter")
    fun findSimilarTexts(
        @RequestBody request: ThresholdSimilarityRequest
    ): Map<String, Any> {
        val queryEmbedding = embeddingModel.embed(request.query)
        val textEmbeddings = embeddingModel.embed(request.texts)
        
        val allResults = request.texts.mapIndexed { index, text ->
            val similarity = SimilarityUtils.cosineSimilarity(queryEmbedding, textEmbeddings[index])
            mapOf(
                "text" to text,
                "similarity" to similarity,
                "similarityPercent" to (similarity * 100)
            )
        }
        
        val filteredResults = allResults
            .filter { (it["similarity"] as Double) >= request.threshold }
            .sortedByDescending { it["similarity"] as Double }
        
        return mapOf(
            "query" to request.query,
            "threshold" to request.threshold,
            "totalTexts" to request.texts.size,
            "filteredCount" to filteredResults.size,
            "results" to filteredResults
        )
    }
}

