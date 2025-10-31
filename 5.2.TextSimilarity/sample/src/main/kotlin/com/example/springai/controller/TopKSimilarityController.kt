package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.TopKSimilarityRequest
import com.example.springai.util.SimilarityUtils

/**
 * Top-K 유사 텍스트 검색 컨트롤러
 */
@RestController
@RequestMapping("/api/similarity/topk")
class TopKSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 유사도 높은 상위 K개 텍스트 찾기
     * POST http://localhost:8080/api/similarity/topk/find
     * Body: {"query": "쿼리 텍스트", "texts": ["텍스트1", "텍스트2", ...], "topK": 5}
     */
    @PostMapping("/find")
    fun findTopKSimilar(
        @RequestBody request: TopKSimilarityRequest
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
        
        val topK = allResults
            .sortedByDescending { it["similarity"] as Double }
            .take(request.topK)
        
        return mapOf(
            "query" to request.query,
            "topK" to request.topK,
            "totalTexts" to request.texts.size,
            "results" to topK.mapIndexed { index, result ->
                mapOf(
                    "rank" to (index + 1),
                    "text" to result["text"],
                    "similarity" to result["similarity"],
                    "similarityPercent" to result["similarityPercent"]
                )
            }
        )
    }
}

