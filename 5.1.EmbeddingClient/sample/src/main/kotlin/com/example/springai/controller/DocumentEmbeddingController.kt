package com.example.springai.controller

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.web.bind.annotation.*
import com.example.springai.model.DocumentRequest

/**
 * 문서 분할 및 임베딩 예제 컨트롤러
 */
@RestController
@RequestMapping("/api/document-embedding")
class DocumentEmbeddingController(
    private val embeddingModel: EmbeddingModel
) {
    
    /**
     * 문서를 청크로 분할하고 각 청크를 임베딩
     * POST http://localhost:8080/api/document-embedding/embed-document
     * Body: {"text": "긴 문서 텍스트...", "chunkSize": 500}
     */
    @PostMapping("/embed-document")
    fun embedDocument(@RequestBody request: DocumentRequest): Map<String, Any> {
        // 문서를 청크로 분할
        val chunks = splitIntoChunks(request.text, request.chunkSize)
        
        // 각 청크를 임베딩
        val embeddings = embeddingModel.embed(chunks)
        
        return mapOf(
            "originalLength" to request.text.length,
            "chunkCount" to chunks.size,
            "chunkSize" to request.chunkSize,
            "chunks" to chunks.mapIndexed { index, chunk ->
                val embedding = embeddings[index]
                mapOf(
                    "index" to index,
                    "text" to chunk,
                    "length" to chunk.length,
                    "dimension" to embedding.size,
                    "sample" to embedding.take(5).map { it.toDouble() }
                )
            }
        )
    }
    
    /**
     * 문서를 간단하게 분할하는 유틸리티 함수
     */
    private fun splitIntoChunks(text: String, chunkSize: Int): List<String> {
        val chunks = mutableListOf<String>()
        var start = 0
        
        while (start < text.length) {
            val end = minOf(start + chunkSize, text.length)
            chunks.add(text.substring(start, end))
            start = end
        }
        
        return chunks
    }
}

