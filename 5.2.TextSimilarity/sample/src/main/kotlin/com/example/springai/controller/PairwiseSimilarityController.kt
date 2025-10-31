package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.PairwiseSimilarityRequest
import com.example.springai.util.SimilarityUtils

/**
 * 모든 텍스트 쌍 간 유사도 계산 컨트롤러
 */
@RestController
@RequestMapping("/api/similarity/pairwise")
class PairwiseSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 모든 텍스트 쌍 간 유사도 계산
     * POST http://localhost:8080/api/similarity/pairwise/calculate
     * Body: {"texts": ["텍스트1", "텍스트2", "텍스트3"]}
     */
    @PostMapping("/calculate")
    fun calculatePairwiseSimilarity(
        @RequestBody request: PairwiseSimilarityRequest
    ): Map<String, Any> {
        val embeddings = embeddingModel.embed(request.texts)
        
        val pairs = mutableListOf<Map<String, Any>>()
        
        for (i in request.texts.indices) {
            for (j in (i + 1) until request.texts.size) {
                val similarity = SimilarityUtils.cosineSimilarity(embeddings[i], embeddings[j])
                pairs.add(
                    mapOf(
                        "text1" to request.texts[i],
                        "text2" to request.texts[j],
                        "text1Index" to i,
                        "text2Index" to j,
                        "similarity" to similarity,
                        "similarityPercent" to (similarity * 100)
                    )
                )
            }
        }
        
        val sortedPairs = pairs.sortedByDescending { it["similarity"] as Double }
        
        val result = mutableMapOf<String, Any>(
            "totalTexts" to request.texts.size,
            "totalPairs" to pairs.size,
            "pairs" to sortedPairs
        )
        
        sortedPairs.firstOrNull()?.let {
            result["mostSimilar"] = it
        }
        sortedPairs.lastOrNull()?.let {
            result["leastSimilar"] = it
        }
        
        return result
    }
}

