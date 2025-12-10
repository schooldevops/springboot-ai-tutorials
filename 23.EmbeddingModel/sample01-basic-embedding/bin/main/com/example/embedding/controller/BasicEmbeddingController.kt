package com.example.embedding.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*

/** Sample 01: Basic Embedding */
@RestController
@RequestMapping("/api/embedding")
class BasicEmbeddingController(private val embeddingModel: EmbeddingModel) {

    @GetMapping("/simple")
    fun embedSimple(@RequestParam text: String): Map<String, Any> {
        val embedding = embeddingModel.embed(text)
        return mapOf(
                "text" to text,
                "dimensions" to embedding.size,
                "vector" to embedding.take(10) // 처음 10개만 표시
        )
    }

    @GetMapping("/dimensions")
    fun getDimensions(): Map<String, Any> {
        val dimensions = embeddingModel.dimensions()
        return mapOf("dimensions" to dimensions, "model" to "text-embedding-3-small")
    }

    @GetMapping("/full-vector")
    fun getFullVector(@RequestParam text: String): Map<String, Any> {
        val embedding = embeddingModel.embed(text)
        return mapOf("text" to text, "dimensions" to embedding.size, "vector" to embedding.toList())
    }
}
