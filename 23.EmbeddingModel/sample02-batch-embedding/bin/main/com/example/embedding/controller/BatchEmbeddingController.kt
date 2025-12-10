package com.example.embedding.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*

/** Sample 02: Batch Embedding */
@RestController
@RequestMapping("/api/batch")
class BatchEmbeddingController(private val embeddingModel: EmbeddingModel) {

    @PostMapping("/embed")
    fun embedBatch(@RequestBody texts: List<String>): Map<String, Any> {
        val embeddings = embeddingModel.embed(texts)
        return mapOf(
                "count" to texts.size,
                "dimensions" to embeddings.first().size,
                "embeddings" to embeddings.map { it.take(5) } // 처음 5개만
        )
    }

    @PostMapping("/embed-response")
    fun embedForResponse(@RequestBody texts: List<String>): Map<String, Any> {
        val response = embeddingModel.embedForResponse(texts)
        return mapOf(
                "resultsCount" to response.results.size,
                "metadata" to mapOf("model" to (response.metadata?.model ?: "unknown")),
                "embeddings" to
                        response.results.map {
                            mapOf(
                                    "index" to it.index,
                                    "dimensions" to it.output.size,
                                    "preview" to it.output.take(5)
                            )
                        }
        )
    }
}
