package com.example.springai.service

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.stereotype.Service

/**
 * 간단한 시맨틱 검색 서비스 예제
 */
@Service
class SimpleSemanticSearchService(
    private val embeddingModel: EmbeddingModel
) {
    // 문서 저장 (실제로는 DB에 저장)
    private val documents = mutableListOf<Document>()
    
    /**
     * 문서 추가
     */
    fun addDocument(text: String, id: String) {
        val embedding = embeddingModel.embed(text)
        documents.add(Document(id, text, embedding))
    }
    
    /**
     * 시맨틱 검색
     */
    fun search(query: String, topK: Int = 5): List<SearchResult> {
        // 검색어 임베딩 생성
        val queryVector = embeddingModel.embed(query)
        
        // 모든 문서와의 유사도 계산
        val results = documents.map { doc ->
            val similarity = cosineSimilarity(queryVector, doc.embedding)
            SearchResult(doc.id, doc.text, similarity)
        }
        
        // 유사도 순으로 정렬하여 상위 K개 반환
        return results.sortedByDescending { it.similarity }.take(topK)
    }
    
    /**
     * 코사인 유사도 계산
     */
    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
        if (a.size != b.size) return 0.0
        
        val dotProduct = a.zip(b).sumOf { (x, y) -> (x * y).toDouble() }
        val normA = kotlin.math.sqrt(a.sumOf { (it * it).toDouble() })
        val normB = kotlin.math.sqrt(b.sumOf { (it * it).toDouble() })
        
        return if (normA > 0.0 && normB > 0.0) {
            dotProduct / (normA * normB)
        } else {
            0.0
        }
    }
    
    /**
     * 모든 문서 조회
     */
    fun getAllDocuments(): List<Document> {
        return documents.toList()
    }
    
    /**
     * 문서 삭제
     */
    fun removeDocument(id: String): Boolean {
        return documents.removeIf { it.id == id }
    }
    
    /**
     * 모든 문서 삭제
     */
    fun clearDocuments() {
        documents.clear()
    }
}

data class Document(
    val id: String,
    val text: String,
    val embedding: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Document) return false
        
        if (id != other.id) return false
        if (text != other.text) return false
        if (!embedding.contentEquals(other.embedding)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + embedding.contentHashCode()
        return result
    }
}

data class SearchResult(
    val id: String,
    val text: String,
    val similarity: Double
)

