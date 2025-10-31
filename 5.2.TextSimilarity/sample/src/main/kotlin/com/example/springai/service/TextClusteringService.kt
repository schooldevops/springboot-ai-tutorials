package com.example.springai.service

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.stereotype.Service
import com.example.springai.util.SimilarityUtils

/**
 * 텍스트 클러스터링 서비스
 */
@Service
class TextClusteringService(
    private val embeddingModel: EmbeddingModel
) {
    fun clusterTexts(
        texts: List<String>,
        similarityThreshold: Double = 0.7
    ): List<Map<String, Any>> {
        val embeddings = embeddingModel.embed(texts)
        val clusters = mutableListOf<Map<String, Any>>()
        val used = BooleanArray(texts.size)
        
        for (i in texts.indices) {
            if (used[i]) continue
            
            val cluster = mutableListOf<Int>()
            cluster.add(i)
            used[i] = true
            
            for (j in (i + 1) until texts.size) {
                if (used[j]) continue
                
                val similarity = SimilarityUtils.cosineSimilarity(embeddings[i], embeddings[j])
                
                if (similarity >= similarityThreshold) {
                    cluster.add(j)
                    used[j] = true
                }
            }
            
            clusters.add(
                mapOf(
                    "clusterId" to clusters.size,
                    "texts" to cluster.map { texts[it] },
                    "centerIndex" to i,
                    "size" to cluster.size
                )
            )
        }
        
        return clusters.sortedByDescending { (it["size"] as Int) }
    }
}

