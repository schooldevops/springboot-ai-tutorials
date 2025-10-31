package com.example.springai.util

/**
 * 유사도 계산 유틸리티
 */
object SimilarityUtils {
    
    /**
     * 코사인 유사도 계산
     */
    fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
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
     * 유사도 해석
     */
    fun interpretSimilarity(similarity: Double): String {
        return when {
            similarity >= 0.9 -> "매우 유사"
            similarity >= 0.7 -> "유사"
            similarity >= 0.5 -> "보통"
            similarity >= 0.3 -> "다소 다름"
            else -> "다름"
        }
    }
}

