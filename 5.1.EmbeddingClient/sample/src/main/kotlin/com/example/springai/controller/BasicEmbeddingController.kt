package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.EmbedRequest
import com.example.springai.model.BatchEmbedRequest

/**
 * EmbeddingModel의 기본 사용법을 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/embedding")
class BasicEmbeddingController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 단일 텍스트 임베딩 생성
     * POST http://localhost:8080/api/embedding/embed
     * Body: {"text": "Spring AI는 Spring 프레임워크를 위한 AI 통합 라이브러리입니다."}
     */
    @PostMapping("/embed")
    fun embedText(@RequestBody request: EmbedRequest): Map<String, Any> {
        val embedding = embeddingModel.embed(request.text)
        
        return mapOf(
            "text" to request.text,
            "dimension" to embedding.size,
            "embedding" to embedding.map { it.toDouble() },
            "sample" to embedding.take(5).map { it.toDouble() } // 처음 5개 값만 샘플로
        )
    }
    
    /**
     * 배치 임베딩 생성
     * POST http://localhost:8080/api/embedding/embed-batch
     * Body: {"texts": ["텍스트1", "텍스트2", "텍스트3"]}
     */
    @PostMapping("/embed-batch")
    fun embedBatch(@RequestBody request: BatchEmbedRequest): Map<String, Any> {
        val embeddings = embeddingModel.embed(request.texts)
        
        val results = mutableListOf<Map<String, Any>>()
        for (i in request.texts.indices) {
            val text = request.texts[i]
            val embedding = embeddings[i]
            results.add(mapOf(
                "text" to text,
                "dimension" to embedding.size,
                "sample" to embedding.take(5).map { it.toDouble() }
            ))
        }
        
        return mapOf(
            "count" to embeddings.size,
            "dimension" to if (embeddings.isNotEmpty()) embeddings[0].size else 0,
            "results" to results
        )
    }
    
    /**
     * 임베딩 정보 확인
     * GET http://localhost:8080/api/embedding/info
     */
    @GetMapping("/info")
    fun getEmbeddingInfo(): Map<String, Any> {
        val testText = "테스트"
        val values = embeddingModel.embed(testText)
        
        return mapOf(
            "dimension" to values.size,
            "sample" to values.take(5).map { it.toDouble() },
            "min" to (values.minOrNull()?.toDouble() ?: 0.0),
            "max" to (values.maxOrNull()?.toDouble() ?: 0.0),
            "average" to values.average().toDouble()
        )
    }
}

