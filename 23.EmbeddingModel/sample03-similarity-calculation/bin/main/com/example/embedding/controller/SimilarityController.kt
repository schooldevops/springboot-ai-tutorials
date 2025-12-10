package com.example.embedding.controller

import com.example.embedding.service.SemanticSearchService
import com.example.embedding.util.SimilarityUtil
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*

/** Sample 03: Similarity Calculation */
@RestController
@RequestMapping("/api/similarity")
class SimilarityController(
        private val embeddingModel: EmbeddingModel,
        private val searchService: SemanticSearchService
) {

    data class SimilarityRequest(val text1: String, val text2: String)

    data class SearchRequest(val query: String, val documents: List<String>, val topK: Int = 3)

    @PostMapping("/cosine")
    fun calculateCosineSimilarity(@RequestBody request: SimilarityRequest): Map<String, Any> {
        val embedding1 = embeddingModel.embed(request.text1)
        val embedding2 = embeddingModel.embed(request.text2)
        val similarity = SimilarityUtil.cosineSimilarity(embedding1, embedding2)

        return mapOf(
                "text1" to request.text1,
                "text2" to request.text2,
                "similarity" to similarity,
                "interpretation" to
                        when {
                            similarity > 0.9 -> "매우 유사"
                            similarity > 0.7 -> "유사"
                            similarity > 0.5 -> "다소 유사"
                            else -> "유사하지 않음"
                        }
        )
    }

    @PostMapping("/search")
    fun semanticSearch(@RequestBody request: SearchRequest): Map<String, Any> {
        val results = searchService.search(request.query, request.documents, request.topK)

        return mapOf(
                "query" to request.query,
                "results" to results.map { mapOf("text" to it.text, "similarity" to it.similarity) }
        )
    }
}
