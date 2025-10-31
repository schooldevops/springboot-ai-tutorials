package com.example.springai.controller

import com.example.springai.service.SimpleSemanticSearchService
import org.springframework.web.bind.annotation.*

/**
 * 시맨틱 검색 컨트롤러
 */
@RestController
@RequestMapping("/api/semantic-search")
class SemanticSearchController(
    private val searchService: SimpleSemanticSearchService
) {
    
    /**
     * 문서 추가
     * POST http://localhost:8080/api/semantic-search/add
     * Body: {"id": "doc1", "text": "Spring AI는 Spring 프레임워크를 위한 AI 통합 라이브러리입니다."}
     */
    @PostMapping("/add")
    fun addDocument(@RequestBody request: AddDocumentRequest): Map<String, String> {
        searchService.addDocument(request.text, request.id)
        return mapOf("status" to "success", "id" to request.id)
    }
    
    /**
     * 시맨틱 검색
     * POST http://localhost:8080/api/semantic-search/search
     * Body: {"query": "프로그래밍", "topK": 5}
     */
    @PostMapping("/search")
    fun search(@RequestBody request: SearchRequest): Map<String, Any> {
        val results = searchService.search(request.query, request.topK)
        
        return mapOf(
            "query" to request.query,
            "topK" to request.topK,
            "results" to results.map { result ->
                mapOf(
                    "id" to result.id,
                    "text" to result.text,
                    "similarity" to result.similarity
                )
            }
        )
    }
    
    /**
     * 모든 문서 조회
     * GET http://localhost:8080/api/semantic-search/documents
     */
    @GetMapping("/documents")
    fun getAllDocuments(): Map<String, Any> {
        val documents = searchService.getAllDocuments()
        
        return mapOf(
            "count" to documents.size,
            "documents" to documents.map { doc ->
                mapOf(
                    "id" to doc.id,
                    "text" to doc.text,
                    "embeddingDimension" to doc.embedding.size
                )
            }
        )
    }
    
    /**
     * 문서 삭제
     * DELETE http://localhost:8080/api/semantic-search/remove/{id}
     */
    @DeleteMapping("/remove/{id}")
    fun removeDocument(@PathVariable id: String): Map<String, Any> {
        val removed = searchService.removeDocument(id)
        return mapOf(
            "success" to removed,
            "id" to id
        )
    }
    
    /**
     * 모든 문서 삭제
     * DELETE http://localhost:8080/api/semantic-search/clear
     */
    @DeleteMapping("/clear")
    fun clearDocuments(): Map<String, String> {
        searchService.clearDocuments()
        return mapOf("status" to "cleared")
    }
}

data class AddDocumentRequest(
    val id: String,
    val text: String
)

data class SearchRequest(
    val query: String,
    val topK: Int = 5
)

