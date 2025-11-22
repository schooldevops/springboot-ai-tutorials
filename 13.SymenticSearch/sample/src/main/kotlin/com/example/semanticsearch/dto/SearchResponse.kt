package com.example.semanticsearch.dto

/**
 * 검색 결과 항목
 */
data class SearchResult(
    val rank: Int,
    val content: String,
    val metadata: Map<String, Any> = emptyMap(),
    val score: Double? = null
)

/**
 * 검색 응답
 */
data class SearchResponse(
    val query: String,
    val resultCount: Int,
    val results: List<SearchResult>
)
