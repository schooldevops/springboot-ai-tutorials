package com.example.springai.service

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.stereotype.Service
import com.example.springai.util.SimilarityUtils

/**
 * 문서 중복 검사 서비스
 */
@Service
class DuplicateDetectionService(
    private val embeddingModel: EmbeddingModel
) {
    fun detectDuplicates(
        texts: List<String>,
        threshold: Double = 0.95
    ): List<Map<String, Any>> {
        val embeddings = embeddingModel.embed(texts)
        
        val duplicates = mutableListOf<Map<String, Any>>()
        
        for (i in texts.indices) {
            for (j in (i + 1) until texts.size) {
                val similarity = SimilarityUtils.cosineSimilarity(embeddings[i], embeddings[j])
                
                if (similarity >= threshold) {
                    duplicates.add(
                        mapOf(
                            "text1" to texts[i],
                            "text2" to texts[j],
                            "text1Index" to i,
                            "text2Index" to j,
                            "similarity" to similarity,
                            "similarityPercent" to (similarity * 100)
                        )
                    )
                }
            }
        }
        
        return duplicates.sortedByDescending { it["similarity"] as Double }
    }
}

