package com.example.springai.model

/**
 * 공통으로 사용되는 데이터 클래스들
 */
data class EmbedRequest(
    val text: String
)

data class BatchEmbedRequest(
    val texts: List<String>
)

data class DocumentRequest(
    val text: String,
    val chunkSize: Int = 500
)

