package com.example.semanticsearch.controller

import com.example.semanticsearch.dto.*
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest as VectorSearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/search")
class DocumentSearchController(
    private val vectorStore: VectorStore
) {

    /**
     * 단일 문서 추가
     * POST /api/search/documents
     */
    @PostMapping("/documents")
    fun addDocument(@RequestBody request: AddDocumentRequest): Map<String, Any> {
        val document = Document(request.content, request.metadata)
        vectorStore.add(listOf(document))
        
        return mapOf(
            "status" to "success",
            "message" to "문서가 추가되었습니다",
            "documentId" to document.id,
            "content" to request.content
        )
    }

    /**
     * 다중 문서 일괄 추가
     * POST /api/search/documents/batch
     */
    @PostMapping("/documents/batch")
    fun addDocuments(@RequestBody request: AddDocumentsRequest): Map<String, Any> {
        val documents = request.documents.map { item ->
            Document(item.content, item.metadata)
        }
        
        vectorStore.add(documents)
        
        return mapOf(
            "status" to "success",
            "message" to "${documents.size}개 문서가 추가되었습니다",
            "count" to documents.size,
            "documentIds" to documents.map { it.id }
        )
    }

    /**
     * 시맨틱 검색 (POST)
     * POST /api/search/query
     */
    @PostMapping("/query")
    fun search(@RequestBody request: SearchRequest): SearchResponse {
        // VectorStore SearchRequest 생성
        val searchRequest = VectorSearchRequest.query(request.query)
            .withTopK(request.topK)
        
        // 유사도 임계값이 있으면 적용
        request.similarityThreshold?.let {
            searchRequest.withSimilarityThreshold(it)
        }
        
        val results = vectorStore.similaritySearch(searchRequest)
        
        val searchResults = results.mapIndexed { index, doc ->
            SearchResult(
                rank = index + 1,
                content = doc.content,
                metadata = doc.metadata
            )
        }
        
        return SearchResponse(
            query = request.query,
            resultCount = searchResults.size,
            results = searchResults
        )
    }

    /**
     * 시맨틱 검색 (GET)
     * GET /api/search/query?query=검색어&topK=5
     */
    @GetMapping("/query")
    fun searchGet(
        @RequestParam query: String,
        @RequestParam(defaultValue = "5") topK: Int,
        @RequestParam(required = false) similarityThreshold: Double?
    ): SearchResponse {
        return search(SearchRequest(query, topK, similarityThreshold))
    }

    /**
     * 헬스 체크
     */
    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf(
            "status" to "UP",
            "service" to "Semantic Search API",
            "timestamp" to System.currentTimeMillis().toString()
        )
    }
}
