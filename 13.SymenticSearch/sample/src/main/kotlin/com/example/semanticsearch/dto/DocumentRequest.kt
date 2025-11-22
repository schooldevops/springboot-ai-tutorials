package com.example.semanticsearch.dto

/**
 * 단일 문서 추가 요청
 */
data class AddDocumentRequest(
    val content: String,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * 다중 문서 추가 요청
 */
data class AddDocumentsRequest(
    val documents: List<DocumentItem>
)

data class DocumentItem(
    val content: String,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * 검색 요청
 */
data class SearchRequest(
    val query: String,
    val topK: Int = 5,
    val similarityThreshold: Double? = null
)
