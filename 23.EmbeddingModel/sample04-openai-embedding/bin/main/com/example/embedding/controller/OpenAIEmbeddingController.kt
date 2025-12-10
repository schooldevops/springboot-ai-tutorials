package com.example.embedding.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*

/**
 * Sample 04: OpenAI Embedding
 *
 * OpenAI 임베딩 모델 사용법
 * - text-embedding-3-small: 1536 dimensions (빠르고 저렴)
 * - text-embedding-3-large: 3072 dimensions (고품질)
 * - text-embedding-ada-002: 1536 dimensions (레거시)
 */
@RestController
@RequestMapping("/api/openai")
class OpenAIEmbeddingController(private val embeddingModel: EmbeddingModel) {

    @GetMapping("/embed")
    fun embed(@RequestParam text: String): Map<String, Any> {
        val embedding = embeddingModel.embed(text)
        return mapOf(
                "provider" to "OpenAI",
                "model" to "text-embedding-3-small",
                "text" to text,
                "dimensions" to embedding.size,
                "vector_preview" to embedding.take(10).toList()
        )
    }

    @PostMapping("/batch")
    fun batchEmbed(@RequestBody texts: List<String>): Map<String, Any> {
        val embeddings = embeddingModel.embed(texts)
        return mapOf(
                "provider" to "OpenAI",
                "count" to texts.size,
                "dimensions" to embeddings.first().size,
                "embeddings" to embeddings.map { it.take(5).toList() }
        )
    }

    @GetMapping("/info")
    fun getInfo(): Map<String, Any> {
        return mapOf(
                "provider" to "OpenAI",
                "model" to "text-embedding-3-small",
                "dimensions" to embeddingModel.dimensions(),
                "pricing" to
                        mapOf(
                                "text-embedding-3-small" to "$0.00002 / 1K tokens",
                                "text-embedding-3-large" to "$0.00013 / 1K tokens"
                        ),
                "features" to
                        listOf(
                                "High quality embeddings",
                                "Multiple model sizes",
                                "Customizable dimensions"
                        )
        )
    }
}
