package com.example.embedding.service

import com.example.embedding.util.SimilarityUtil
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.stereotype.Service

/** 시맨틱 검색 서비스 */
@Service
class SemanticSearchService(private val embeddingModel: EmbeddingModel) {

    data class SearchResult(val text: String, val similarity: Double)

    /** 문서 목록에서 쿼리와 가장 유사한 문서 찾기 */
    fun search(query: String, documents: List<String>, topK: Int = 3): List<SearchResult> {
        // 1. 쿼리 임베딩
        val queryEmbedding = embeddingModel.embed(query)

        // 2. 문서들 임베딩
        val documentEmbeddings = embeddingModel.embed(documents)

        // 3. 유사도 계산 및 정렬
        return documents
                .mapIndexed { index, doc ->
                    val similarity =
                            SimilarityUtil.cosineSimilarity(
                                    queryEmbedding,
                                    documentEmbeddings[index]
                            )
                    SearchResult(doc, similarity)
                }
                .sortedByDescending { it.similarity }
                .take(topK)
    }
}
