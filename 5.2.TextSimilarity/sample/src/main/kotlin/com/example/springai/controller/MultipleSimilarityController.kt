package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.MultipleSimilarityRequest
import com.example.springai.util.SimilarityUtils

/**
 * 여러 텍스트 간 유사도 비교 컨트롤러
 */
@RestController
@RequestMapping("/api/similarity/multiple")
class MultipleSimilarityController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 하나의 쿼리와 여러 텍스트 간 유사도 비교
     * POST http://localhost:8080/api/similarity/multiple/compare
     * Body: {"query": "쿼리 텍스트", "texts": ["텍스트1", "텍스트2", "텍스트3"]}
     */
    @PostMapping("/compare")
    fun compareMultiple(
        @RequestBody request: MultipleSimilarityRequest
    ): Map<String, Any> {
        val queryEmbedding = embeddingModel.embed(request.query)
        val textEmbeddings = embeddingModel.embed(request.texts)
        
        val results = request.texts.mapIndexed { index, text ->
            val similarity = SimilarityUtils.cosineSimilarity(queryEmbedding, textEmbeddings[index])
            mapOf(
                "index" to index,
                "text" to text,
                "similarity" to similarity,
                "similarityPercent" to (similarity * 100),
                "interpretation" to SimilarityUtils.interpretSimilarity(similarity)
            )
        }
        
        val sortedResults = results.sortedByDescending { it["similarity"] as Double }
        val mostSimilar = sortedResults.firstOrNull()
        
        return mapOf(
            "query" to request.query,
            "totalTexts" to request.texts.size,
            "results" to sortedResults,
            "mostSimilar" to (mostSimilar?.let {
                mapOf(
                    "text" to it["text"],
                    "similarity" to it["similarity"],
                    "similarityPercent" to it["similarityPercent"]
                )
            } ?: emptyMap<String, Any>())
        )
    }
}

